package com.logilink.slack.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SendMessageReq {

	@NotNull(message = "userId는 필수입니다.")
	private Long userId;

	@NotBlank(message = "message는 비어 있을 수 없습니다.")
	private String message;
}
