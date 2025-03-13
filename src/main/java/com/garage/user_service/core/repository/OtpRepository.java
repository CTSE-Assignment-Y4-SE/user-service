package com.garage.user_service.core.repository;

import com.garage.user_service.core.model.Otp;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {

	Optional<Otp> findByEmailAndUsedAndExpiresAtAfter(@NotNull String email, boolean isExpired,
			LocalDateTime currentTime);

	List<Otp> findAllByEmailAndUsedFalseAndExpiresAtBefore(String email, LocalDateTime now);

	List<Otp> findAllByEmailAndUsedFalse(String email);

}
