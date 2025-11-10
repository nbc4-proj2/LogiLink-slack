package com.logilink.slack.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "p_slack_user_link",
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_user_id", columnNames = {"user_id", "email"})
	}
)
@Getter
@NoArgsConstructor
public class SlackUserLink {

	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "slack_user_link_id")
	private UUID id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "email", nullable = false, length = 100)
	private String email;

	@Column(name = "slack_user_id", nullable = false, length = 50)
	private String slackUserId;

	@Column(name = "is_active", nullable = false)
	private boolean isActive = true;

	@Column(name = "linked_at", nullable = false)
	private LocalDateTime linkedAt = LocalDateTime.now();

	@Column(name = "last_verified_at")
	private LocalDateTime lastVerifiedAt = LocalDateTime.now();

	protected SlackUserLink(Long userId, String email, String slackUserId) {
		this.userId = userId;
		this.email = email;
		this.slackUserId = slackUserId;
	}

	public static SlackUserLink create(Long userId, String email, String slackUserId) {
		SlackUserLink link = new SlackUserLink();
		link.userId = userId;
		link.email = email;
		link.slackUserId = slackUserId;
		link.isActive = true;
		link.linkedAt = LocalDateTime.now();
		link.lastVerifiedAt = LocalDateTime.now();
		return link;
	}

	public void deactivate() {
		this.isActive = false;
	}

	public void activate() {
		this.isActive = true;
		this.lastVerifiedAt = LocalDateTime.now();
	}

	public void updateSlackUserId(String slackUserId) {
		this.slackUserId = slackUserId;
		this.lastVerifiedAt = LocalDateTime.now();
	}
}
