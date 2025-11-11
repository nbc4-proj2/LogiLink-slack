package com.logilink.slack.dto.response;

import lombok.Getter;

@Getter
public class PostMessageRes {
	private boolean ok;
	private String channel;
	private String ts;
	private String error;
}