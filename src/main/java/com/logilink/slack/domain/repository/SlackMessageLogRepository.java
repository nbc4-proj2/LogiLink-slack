package com.logilink.slack.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logilink.slack.domain.entity.SlackMessage;

public interface SlackMessageLogRepository extends JpaRepository<SlackMessage, UUID> {
}
