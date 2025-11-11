package com.logilink.slack.service;

import org.springframework.stereotype.Service;

import com.logilink.slack.client.SlackClient;
import com.logilink.slack.common.exception.AppException;
import com.logilink.slack.domain.entity.SlackUserLink;
import com.logilink.slack.domain.repository.SlackUserLinkRepository;
import com.logilink.slack.exception.SlackErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SlackMessageService {

	private final SlackClient slackClient;
	private final SlackUserLinkRepository slackUserLinkRepository;

	/**
	 * 특정 사용자에게 Slack 메시지를 전송합니다.
	 */
	public void sendToUser(Long userId, String text) {
		SlackUserLink link = slackUserLinkRepository.findByUserId(userId)
													.orElseThrow(() -> AppException.of(SlackErrorCode.SLACK_USER_NOT_LINKED));

		if (!link.isActive()) {
			throw AppException.of(SlackErrorCode.SLACK_USER_INVALID_STATE);
		}

		slackClient.sendMessageToUser(link.getSlackUserId(), text);
	}
}
