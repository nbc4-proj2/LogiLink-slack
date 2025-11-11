package com.logilink.slack.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logilink.slack.domain.entity.SlackMessage;

public interface SlackMessageRepository extends JpaRepository<SlackMessage, UUID> {
}
