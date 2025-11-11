package com.logilink.slack.dto.response;

import lombok.Getter;

@Getter
public class OpenConversationRes {
	private boolean ok;
	private Channel channel;
	private String error;

	@Getter
	public static class Channel {
		private String id;
	}
}
