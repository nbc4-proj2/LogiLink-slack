package com.logilink.slack.client;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.logilink.slack.dto.response.LookupByEmailRes;
import com.logilink.slack.properties.SlackProperties;

import lombok.RequiredArgsConstructor;

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
}
