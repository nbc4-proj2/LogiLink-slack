package com.logilink.slack.dto.request;

import jakarta.validation.constraints.Email;

public record SlackUserLinkUpdateReq(
	@Email
	String email) {
}
