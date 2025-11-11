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
import com.logilink.slack.dto.response.PostMessageRes;
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

	public OpenConversationRes openConversation(String slackUserId) {
		String url = slackProperties.getBaseUrl() + "/conversations.open";
		Map<String, Object> body = Map.of("users", slackUserId);

		ResponseEntity<OpenConversationRes> res = restTemplate.exchange(
			url,
			HttpMethod.POST,
			new HttpEntity<>(body, createHeaders()),
			OpenConversationRes.class
		);
		OpenConversationRes resBody = res.getBody();
		if (resBody == null || !resBody.isOk() || resBody.getChannel() == null || resBody.getChannel().getId() == null) {
			throw AppException.of(SlackErrorCode.SLACK_OPEN_DM_FAILED);
		}
		return resBody;
	}

	/**
	 * Slack DM 메시지 전송
	 */
	public PostMessageRes postMessage(String slackUserId, String text) {
		String url = slackProperties.getBaseUrl() + "/chat.postMessage";

		Map<String, Object> body = Map.of(
			"channel", slackUserId,
			"text", text
		);

		try {
			ResponseEntity<PostMessageRes> res = restTemplate.exchange(
				url, HttpMethod.POST, new HttpEntity<>(body, createHeaders()),
				PostMessageRes.class
			);
			PostMessageRes resBody = res.getBody();

			if (resBody == null) {
				log.error("Slack postMessage null body");
				throw AppException.of(SlackErrorCode.SLACK_MESSAGE_SEND_FAILED);
			}
			if (!resBody.isOk()) {
				log.error("Slack postMessage failed: error={}", resBody.getError());
				throw AppException.of(SlackErrorCode.SLACK_MESSAGE_SEND_FAILED);
			}
			log.info("✅ Slack 메시지 전송 완료: userId={}, status={}", slackUserId, res.getStatusCode());
			log.debug("Slack response body: {}", res.getBody());
			return resBody;
		} catch (Exception e) {
			log.error("❌ Slack 메시지 전송 실패: userId={}, reason={}", slackUserId, e.getMessage());
			throw new RuntimeException("Slack 메시지 전송 실패", e);
		}
	}

	public PostMessageRes sendMessageToUser(String slackUserId, String text) {
		OpenConversationRes dmChannel = openConversation(slackUserId);
		return postMessage(dmChannel.getChannel().getId(), text);
	}
}
