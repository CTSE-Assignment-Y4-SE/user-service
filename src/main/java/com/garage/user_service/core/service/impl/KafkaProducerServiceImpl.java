package com.garage.user_service.core.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garage.user_service.core.payload.request.EmailRequestDto;
import com.garage.user_service.core.service.KafkaProducerService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerServiceImpl implements KafkaProducerService {

	@NonNull
	private final KafkaTemplate<String, String> kafkaTemplate;

	@Override
	public void sendEmail(EmailRequestDto emailRequestDto) {
		log.debug("KafkaProducerServiceImpl.sendEmail(): execution started");

		try {
			String emailRequestJson = new ObjectMapper().writeValueAsString(emailRequestDto);
			kafkaTemplate.send("user-notifications", emailRequestJson);
		}
		catch (JsonProcessingException e) {
			log.error("kafkaProducerServiceImpl.sendEmail(): execution failed- {}", e.getMessage());
		}

		log.debug("KafkaProducerServiceImpl.sendEmail(): execution ended");
	}

}
