package com.logilink.slack.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logilink.slack.service.SlackApiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/slack")
@RequiredArgsConstructor
public class SlackController {
	private final SlackApiService slackApiService;

	@PostMapping("/send")
	public String send(@RequestParam String channelId, @RequestParam String message) {
		slackApiService.sendMessage(channelId, message);
		return "Message sent!";
	}
}
