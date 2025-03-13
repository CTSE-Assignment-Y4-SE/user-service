package com.garage.user_service.core.service.impl;

import com.garage.user_service.core.service.VehicleOwnerService;
import com.garage.vehicle_owner_service.core.grpc.server.VehicleOwnerAccountCreateRequest;
import com.garage.vehicle_owner_service.core.grpc.server.VehicleOwnerAccountCreateResponse;
import com.garage.vehicle_owner_service.core.grpc.server.VehicleOwnerAccountListResponse;
import com.garage.vehicle_owner_service.core.grpc.server.VehicleOwnerAccountServiceGrpc;
import com.google.protobuf.Empty;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class VehicleOwnerServiceImpl implements VehicleOwnerService {

	@NonNull
	private final VehicleOwnerAccountServiceGrpc.VehicleOwnerAccountServiceBlockingStub vehicleOwnerAccountServiceBlockingStub;

	@Override
	public VehicleOwnerAccountCreateResponse createVehicleOwnerAccount(@NonNull Long userId) {
		VehicleOwnerAccountCreateRequest request = VehicleOwnerAccountCreateRequest.newBuilder()
			.setUserId(userId)
			.build();
		return vehicleOwnerAccountServiceBlockingStub.createVehicleOwnerAccount(request);
	}

	@Override
	public VehicleOwnerAccountListResponse GetAllVehicleOwners() {
		return vehicleOwnerAccountServiceBlockingStub.getAllVehicleOwners(Empty.getDefaultInstance());
	}

}
