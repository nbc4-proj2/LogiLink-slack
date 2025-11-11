package com.logilink.slack.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logilink.slack.client.SlackClient;
import com.logilink.slack.common.exception.AppException;
import com.logilink.slack.domain.entity.SlackUserLink;
import com.logilink.slack.domain.repository.SlackUserLinkRepository;
import com.logilink.slack.dto.response.LookupByEmailRes;
import com.logilink.slack.exception.SlackErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SlackLinkService {

	private final SlackClient slackClient;
	private final SlackUserLinkRepository slackUserLinkRepository;

	@Transactional
	public SlackUserLink linkSlackAccount(Long userId, String email) {
		// 이미 연동되어 있을경우
		if (slackUserLinkRepository.existsByEmail(email)) {
			throw AppException.of(SlackErrorCode.SLACK_USER_ALREADY_LINKED);
		}

		LookupByEmailRes res = slackClient.lookupByEmail(email);
		if (res == null || !res.ok()) {
			throw AppException.of(SlackErrorCode.SLACK_LOOKUP_FAILED);
		}

		SlackUserLink link = SlackUserLink.create(userId,email,res.user().id());
		return slackUserLinkRepository.save(link);
	}
}
