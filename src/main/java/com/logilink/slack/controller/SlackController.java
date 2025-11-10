package com.logilink.slack.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logilink.slack.common.BaseResponse;
import com.logilink.slack.domain.entity.SlackUserLink;
import com.logilink.slack.dto.request.SlackLinkReq;
import com.logilink.slack.service.SlackApiService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/slack")
@RequiredArgsConstructor
public class SlackController {
	private final SlackApiService slackApiService;

	@PostMapping("/link")
	public ResponseEntity<BaseResponse<SlackUserLink>> linkSlackUser(
		@Valid @RequestBody SlackLinkReq request) {
		SlackUserLink link = slackApiService.linkSlackAccount(
			request.userId(),
			request.email()
		);

		return ResponseEntity.ok(BaseResponse.success(link));
	}
}
