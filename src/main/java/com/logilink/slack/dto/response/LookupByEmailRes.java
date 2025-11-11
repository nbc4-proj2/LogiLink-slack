package com.logilink.slack.dto.response;

import lombok.Builder;

@Builder
public record LookupByEmailRes(
	boolean ok,
	SlackUser user,
	String error
) {
	@Builder
	public record SlackUser(
		String id,
		String name
	) {}
}
