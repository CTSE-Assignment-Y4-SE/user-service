package com.garage.user_service.core.service;

import com.garage.vehicle_owner_service.core.grpc.server.VehicleOwnerAccountCreateResponse;
import com.garage.vehicle_owner_service.core.grpc.server.VehicleOwnerAccountListResponse;
import lombok.NonNull;

public interface VehicleOwnerService {

	VehicleOwnerAccountCreateResponse createVehicleOwnerAccount(@NonNull Long userId);

	VehicleOwnerAccountListResponse GetAllVehicleOwners();

}
