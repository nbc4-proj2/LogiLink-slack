package com.logilink.slack.client;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.logilink.slack.common.exception.AppException;
import com.logilink.slack.dto.response.LookupByEmailRes;
import com.logilink.slack.dto.response.OpenConversationRes;
import com.logilink.slack.exception.SlackErrorCode;
import com.logilink.slack.properties.SlackProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackClient {

	private final RestTemplate restTemplate;
	private final SlackProperties slackProperties;

	private HttpHeaders createHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(slackProperties.getBotToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	public LookupByEmailRes lookupByEmail(String email) {
		String url = slackProperties.getBaseUrl() + "/users.lookupByEmail?email={email}";
		ResponseEntity<LookupByEmailRes> response = restTemplate.exchange(
			url,
			HttpMethod.GET,
			new HttpEntity<>(createHeaders()),
			LookupByEmailRes.class,
			Map.of("email", email)
		);
		return response.getBody();
	}

	public String openConversation(String slackUserId) {
		String url = slackProperties.getBaseUrl() + "/conversations.open";
		Map<String, Object> body = Map.of("users", slackUserId);

		ResponseEntity<OpenConversationRes> res = restTemplate.exchange(
			url,
			HttpMethod.POST,
			new HttpEntity<>(body, createHeaders()),
			OpenConversationRes.class
		);
		OpenConversationRes b = res.getBody();
		if (b == null || !b.isOk() || b.getChannel() == null || b.getChannel().getId() == null) {
			// 프로젝트의 에러코드에 맞게 교체
			throw AppException.of(SlackErrorCode.SLACK_OPEN_DM_FAILED);
		}
		return b.getChannel().getId();
	}

	/**
	 * Slack DM 메시지 전송
	 */
	public void sendMessage(String slackUserId, String text) {
		String url = slackProperties.getBaseUrl() + "/chat.postMessage";

		Map<String, Object> body = Map.of(
			"channel", slackUserId,
			"text", text
		);

		try {
			ResponseEntity<String> response = restTemplate.exchange(
				url,
				HttpMethod.POST,
				new HttpEntity<>(body, createHeaders()),
				String.class
			);

			log.info("✅ Slack 메시지 전송 완료: userId={}, status={}", slackUserId, response.getStatusCode());
			log.debug("Slack response body: {}", response.getBody());
		} catch (Exception e) {
			log.error("❌ Slack 메시지 전송 실패: userId={}, reason={}", slackUserId, e.getMessage());
			throw new RuntimeException("Slack 메시지 전송 실패", e);
		}
	}

	public void sendMessageToUser(String slackUserId, String text) {
		String dmChannelId = openConversation(slackUserId);
		sendMessage(dmChannelId, text);
	}
}
