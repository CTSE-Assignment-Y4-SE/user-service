package com.garage.user_service.core.service;

import com.garage.user_service.core.payload.request.EmailRequestDto;

public interface NotificationService {

	void sendEmailAsync(EmailRequestDto emailRequestDto);

}
