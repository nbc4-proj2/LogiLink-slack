package com.logilink.slack.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logilink.slack.domain.entity.SlackUserLink;

public interface SlackUserLinkRepository extends JpaRepository<SlackUserLink, UUID> {

	Optional<SlackUserLink> findByUserId(Long userId);
	Optional<SlackUserLink> findByEmail(String email);
	boolean existsByEmail(String email);
}
