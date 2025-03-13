package com.garage.user_service.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp")
@Setter
@Getter
public class Otp {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "otp_id")
	private Long otpId;

	@Column(name = "email", updatable = false, nullable = false)
	private String email;

	@Column(name = "otp_code", updatable = false, nullable = false)
	private String otpCode;

	@Column(name = "expires_at", updatable = false, nullable = false)
	private LocalDateTime expiresAt;

	@Column(name = "used", nullable = false)
	private Boolean used;

	@Column(name = "created_at", updatable = false, nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

}
