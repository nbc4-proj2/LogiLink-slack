package com.logilink.slack.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    String getCode();
    String getMessage();
    HttpStatus getStatus();
}
