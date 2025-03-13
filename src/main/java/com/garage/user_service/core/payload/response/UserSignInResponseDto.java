package com.garage.user_service.core.payload.response;

import com.garage.user_service.core.type.Role;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserSignInResponseDto {

	private Long userId;

	private String email;

	private Role role;

	private String accessToken;

}
