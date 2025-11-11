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
@Table(name = "p_slack_message_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class SlackMessageLog {

	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "slack_message_log_id")
	private UUID id;

	@Column(name = "message_id", nullable = false)
	private UUID messageId; // SlackMessage의 ID 참조 (FK처럼 사용)

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private MessageStatus status;

	@Column(name = "response_code")
	private String responseCode; // Slack 응답 코드나 상태 텍스트

	@Column(name = "response_message", columnDefinition = "TEXT")
	private String responseMessage; // Slack 응답 내용

	@Column(name = "logged_at", nullable = false)
	private LocalDateTime loggedAt;

	public static SlackMessageLog of(SlackMessage message, MessageStatus status, String code, String msg) {
		return SlackMessageLog.builder()
							  .messageId(message.getId())
							  .status(status)
							  .responseCode(code)
							  .responseMessage(msg)
							  .loggedAt(LocalDateTime.now())
							  .build();
	}
}
