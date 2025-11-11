package com.logilink.slack.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logilink.slack.client.SlackClient;
import com.logilink.slack.common.exception.AppException;
import com.logilink.slack.domain.entity.SlackUserLink;
import com.logilink.slack.domain.repository.SlackUserLinkRepository;
import com.logilink.slack.dto.request.SlackUserLinkUpdateReq;
import com.logilink.slack.dto.response.LookupByEmailRes;
import com.logilink.slack.dto.response.SlackUserLinkRes;
import com.logilink.slack.exception.SlackErrorCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SlackLinkService {

	private final SlackClient slackClient;
	private final SlackUserLinkRepository slackUserLinkRepository;

	@Transactional
	public SlackUserLinkRes linkSlackAccount(Long userId, String email) {
		// 이미 연동되어 있을경우
		if (slackUserLinkRepository.existsByEmail(email)) {
			throw AppException.of(SlackErrorCode.SLACK_USER_ALREADY_LINKED);
		}

		LookupByEmailRes res = slackClient.lookupByEmail(email);
		if (res == null || !res.ok()) {
			throw AppException.of(SlackErrorCode.SLACK_LOOKUP_FAILED);
		}

		SlackUserLink link = SlackUserLink.create(userId, email, res.user().id());
		SlackUserLink save = slackUserLinkRepository.save(link);

		return SlackUserLinkRes.from(save);
	}

	@Transactional(readOnly = true)
	public SlackUserLinkRes getLink(Long userId) {
		SlackUserLink link = slackUserLinkRepository.findByUserId(userId)
													.orElseThrow(
														() -> AppException.of(SlackErrorCode.SLACK_USER_NOT_LINKED));
		return SlackUserLinkRes.from(link);
	}

	@Transactional
	public void unlink(Long userId) {
		SlackUserLink link = slackUserLinkRepository.findByUserId(userId)
													.orElseThrow(
														() -> AppException.of(SlackErrorCode.SLACK_USER_NOT_LINKED));

		// 이미 비활성화 상태일 경우 그냥 리턴
		if (!link.isActive())
			return;

		link.deactivate();
		slackUserLinkRepository.save(link);
	}

	public SlackUserLinkRes update(Long userId, @Valid SlackUserLinkUpdateReq request) {
		SlackUserLink link = slackUserLinkRepository.findByUserId(userId)
													.orElseThrow(
														() -> AppException.of(SlackErrorCode.SLACK_USER_NOT_LINKED));

		LookupByEmailRes res = slackClient.lookupByEmail(request.email());

		// 새 slackUserId가 다른 사용자에게 이미 연결된 경우 방지
		slackUserLinkRepository.findBySlackUserId(res.user().id()).ifPresent(existing -> {
			if (!existing.getUserId().equals(userId) && existing.isActive()) {
				throw AppException.of(SlackErrorCode.SLACK_USER_ALREADY_LINKED);
			}
		});

		link.updateSlackUserId(res.user().id());
		link.activate(); // 갱신 시 비활성 상태였다면 재활성
		return SlackUserLinkRes.from(slackUserLinkRepository.save(link));
	}
}
