package com.logilink.slack.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.logilink.slack.domain.entity.MessageStatus;
import com.logilink.slack.domain.entity.SlackMessage;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SlackMessageSendRes {

	private UUID messageId;
	private Long userId;
	private String slackUserId;
	private String text;
	private String slackTs;
	private MessageStatus status;
	private LocalDateTime sentAt;

	public static SlackMessageSendRes from(SlackMessage m) {
		return SlackMessageSendRes.builder()
								  .messageId(m.getId())
								  .userId(m.getUserId())
								  .slackUserId(m.getSlackUserId())
								  .text(m.getContent())
								  .slackTs(m.getSlackTs())
								  .status(m.getStatus())
								  .sentAt(m.getSentAt())
								  .build();
	}
}
