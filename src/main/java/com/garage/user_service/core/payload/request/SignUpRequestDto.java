package com.garage.user_service.core.payload.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignUpRequestDto {

	private String email;

	private String password;

}
