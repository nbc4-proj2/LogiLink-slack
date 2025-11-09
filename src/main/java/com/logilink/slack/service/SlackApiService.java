package com.logilink.slack.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SlackApiService {

	private final RestTemplate restTemplate;

	@Value("${slack.bot-token}")
	private String botToken;

	private static final String SLACK_API_BASE_URL = "https://slack.com/api";

	public void sendMessage(String channelId, String message) {
		String url = SLACK_API_BASE_URL + "/chat.postMessage";

		// 요청 본문
		Map<String, Object> payload = Map.of(
			"channel", channelId,
			"text", message
		);

		// 헤더 설정
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(botToken);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(
				url,
				HttpMethod.POST,
				request,
				String.class
			);

			System.out.println("✅ Slack response: " + response.getBody());
		} catch (Exception e) {
			System.err.println("❌ Slack API error: " + e.getMessage());
		}
	}
}
