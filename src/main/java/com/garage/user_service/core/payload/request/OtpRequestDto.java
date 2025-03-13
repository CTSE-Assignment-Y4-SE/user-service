package com.garage.user_service.core.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OtpRequestDto {

	@NotNull
	private String email;

	@NotNull
	private String otpCode;

}
