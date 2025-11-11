package com.logilink.slack.exception;

import org.springframework.http.HttpStatus;

import com.logilink.slack.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SlackErrorCode implements ErrorCode {

	SLACK_USER_NOT_LINKED("S001", HttpStatus.NOT_FOUND, "연동된 Slack 계정이 없습니다."),
	SLACK_MESSAGE_SEND_FAILED("S002", HttpStatus.INTERNAL_SERVER_ERROR, "Slack 메시지 전송에 실패했습니다."),
	SLACK_CHANNEL_OPEN_FAILED("S003", HttpStatus.INTERNAL_SERVER_ERROR, "Slack 채널 열기에 실패했습니다."),
	SLACK_USER_ALREADY_LINKED("S004", HttpStatus.CONFLICT, "이미 Slack 계정이 연동되어 있습니다."),
	SLACK_LOOKUP_FAILED("S005", HttpStatus.BAD_REQUEST, "Slack 사용자 조회에 실패했습니다."),
	SLACK_USER_INVALID_STATE("S006", HttpStatus.BAD_REQUEST, "비활성화된 Slack 계정입니다."),
	SLACK_OPEN_DM_FAILED("S007", HttpStatus.BAD_GATEWAY, "Slack DM 채널 열기에 실패했습니다."),
	SLACK_API_ERROR("S012", HttpStatus.BAD_GATEWAY, "Slack API 오류가 발생했습니다.");

	private final String code;
	private final HttpStatus status;
	private final String message;
}
