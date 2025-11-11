package com.logilink.slack.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.logilink.slack.domain.entity.SlackUserLink;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SlackUserLinkRes {

	private UUID linkId;
	private Long userId;
	private String email;
	private String slackUserId;
	private boolean active;
	private LocalDateTime linkedAt;

	public static SlackUserLinkRes from(SlackUserLink l) {
		return SlackUserLinkRes.builder()
							   .linkId(l.getId())
							   .userId(l.getUserId())
							   .email(l.getEmail())
							   .slackUserId(l.getSlackUserId())
							   .active(l.isActive())
							   .linkedAt(l.getLinkedAt())
							   .build();
	}
}
