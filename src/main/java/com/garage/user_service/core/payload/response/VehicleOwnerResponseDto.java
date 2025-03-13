package com.garage.user_service.core.payload.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VehicleOwnerResponseDto {

	private Long vehicleOwnerId;

	private String firstName;

	private String lastName;

	private String phoneNumber;

}
