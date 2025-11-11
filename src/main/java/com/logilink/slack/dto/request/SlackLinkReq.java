package com.logilink.slack.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record SlackLinkReq(@NotNull
							   Long userId,
						   @Email
							   String email) {
}


