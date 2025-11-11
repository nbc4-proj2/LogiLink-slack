package com.logilink.slack.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_slack_message")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class SlackMessage {

	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "slack_message_id")
	private UUID id;

	@Column(name = "user_id", nullable = false)
	private Long userId; // 우리 시스템 유저 ID

	@Column(name = "slack_user_id", nullable = false, length = 50)
	private String slackUserId; // 슬랙 내 대상자 ID

	@Column(name = "channel_id", length = 100)
	private String channelId; // DM 채널 or 채널 ID

	@Column(name = "content", nullable = false, columnDefinition = "TEXT")
	private String content; // 메시지 본문

	@Column(name = "slack_ts")
	private String slackTs;    // 메세지 타임스탬프. 슬랙에선 식별자 같은 역할.

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 30)
	private MessageStatus status; // SENT, FAILED 등

	@Column(name = "sent_at")
	private LocalDateTime sentAt;

	@Column(name = "last_attempt_at")
	private LocalDateTime lastAttemptAt;

	public static SlackMessage create(Long userId, String slackUserId, String content) {
		return SlackMessage.builder()
						   .userId(userId)
						   .slackUserId(slackUserId)
						   .content(content)
						   .status(MessageStatus.PENDING)
						   .lastAttemptAt(LocalDateTime.now())
						   .build();
	}

	public void markSent(String channelId, String ts) {
		this.status = MessageStatus.SENT;
		this.channelId = channelId;
		this.slackTs = ts;
		this.sentAt = LocalDateTime.now();
	}

	public void markFailed() {
		this.status = MessageStatus.FAILED;
		this.lastAttemptAt = LocalDateTime.now();
	}
}
