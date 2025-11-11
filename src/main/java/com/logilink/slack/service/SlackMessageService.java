package com.logilink.slack.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logilink.slack.client.SlackClient;
import com.logilink.slack.common.exception.AppException;
import com.logilink.slack.domain.entity.LogActionType;
import com.logilink.slack.domain.entity.MessageStatus;
import com.logilink.slack.domain.entity.SlackMessage;
import com.logilink.slack.domain.entity.SlackMessageLog;
import com.logilink.slack.domain.entity.SlackUserLink;
import com.logilink.slack.domain.repository.SlackMessageLogRepository;
import com.logilink.slack.domain.repository.SlackMessageRepository;
import com.logilink.slack.domain.repository.SlackUserLinkRepository;
import com.logilink.slack.dto.response.OpenConversationRes;
import com.logilink.slack.dto.response.PostMessageRes;
import com.logilink.slack.dto.response.SlackMessageSendRes;
import com.logilink.slack.exception.SlackErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackMessageService {

	private final SlackClient slackClient;
	private final SlackUserLinkRepository linkRepository;
	private final SlackMessageRepository messageRepository;
	private final SlackMessageLogRepository messageLogRepository;

	/**
	 * 특정 사용자에게 Slack 메시지를 전송합니다.
	 */
	@Transactional
	public SlackMessageSendRes sendToUser(Long userId, String text) {
		// 1. 유저-슬랙 연동 확인
		SlackUserLink link = linkRepository.findByUserId(userId)
										   .orElseThrow(() -> AppException.of(SlackErrorCode.SLACK_USER_NOT_LINKED));
		if (!link.isActive()) {
			throw AppException.of(SlackErrorCode.SLACK_USER_INVALID_STATE);
		}

		// 2. 메시지 PENDING 상태로 일단 저장
		SlackMessage message = messageRepository.save(
			SlackMessage.create(userId, link.getSlackUserId(), text)
		);

		try {
			// 3. DM 채널 오픈
			OpenConversationRes openRes = slackClient.openConversation(link.getSlackUserId());
			String channelId = openRes.getChannel().getId();
			saveLog(message, LogActionType.OPEN_DM, MessageStatus.PENDING, "OPEN_DM_OK", "channelId=" + channelId);
			// 4. 메시지 전송
			PostMessageRes postRes = slackClient.postMessage(channelId, text);
			message.markSent(channelId, postRes.getTs());
			messageRepository.save(message);
			saveLog(message, LogActionType.POST_MESSAGE, MessageStatus.SENT, "POST_MESSAGE_OK", "ts=" + postRes.getTs());

			// 5. DTO 변환 후 리턴
			return SlackMessageSendRes.from(message);

		} catch (AppException ae) {
			failAndLog(message, LogActionType.POST_MESSAGE, "POST_MESSAGE_FAILED", ae.getMessage());
			throw ae; // 매핑된 예외 그대로 전달
		} catch (Exception e) {
			failAndLog(message, LogActionType.POST_MESSAGE, "SLACK_API_ERROR", e.getMessage());
			throw AppException.of(SlackErrorCode.SLACK_MESSAGE_SEND_FAILED);
		}
	}

	private void saveLog(SlackMessage message, LogActionType action, MessageStatus status, String code, String detail) {
		messageLogRepository.save(SlackMessageLog.of(message, action, status, code, detail));
	}

	private void failAndLog(SlackMessage message, LogActionType action, String code, String errorDetail) {
		try {
			message.markFailed();
			messageRepository.save(message);
		} catch (Exception ex) {
			log.warn("[SlackMessage] FAILED로 상태 변경 실패: {}", ex.getMessage());
		}

		messageLogRepository.save(
			SlackMessageLog.of(message, action, MessageStatus.FAILED, code, safe(errorDetail))
		);
	}

	private String safe(String msg) {
		if (msg == null) return "";
		return msg.length() > 1000 ? msg.substring(0, 1000) : msg;
	}
}