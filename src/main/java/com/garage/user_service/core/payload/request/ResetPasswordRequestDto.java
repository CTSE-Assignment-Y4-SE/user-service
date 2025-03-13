package com.garage.user_service.core.payload.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResetPasswordRequestDto {

	private String currentPassword;

	private String newPassword;

}
