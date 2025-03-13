package com.garage.user_service.core.service.impl;

import com.garage.user_service.core.payload.request.EmailRequestDto;
import com.garage.user_service.core.service.KafkaProducerService;
import com.garage.user_service.core.service.NotificationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

	@NonNull
	private final KafkaProducerService kafkaProducerService;

	@NonNull
	private final ExecutorService executorService;

	@Override
	public void sendEmailAsync(EmailRequestDto emailRequestDto) {
		log.info("NotificationServiceImpl.sendEmailAsync(): execution started");

		executorService.execute(() -> {
			try {
				kafkaProducerService.sendEmail(emailRequestDto);
				log.info("NotificationServiceImpl.sendEmailAsync(): notification sent successfully");
			}
			catch (Exception e) {
				log.error("NotificationServiceImpl.sendEmailAsync(): failed to send notification", e);
			}
		});

		log.info("NotificationServiceImpl.sendEmailAsync(): execution ended");
	}

}
