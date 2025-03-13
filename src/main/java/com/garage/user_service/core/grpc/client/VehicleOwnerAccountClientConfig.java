package com.garage.user_service.core.grpc.client;

import com.garage.vehicle_owner_service.core.grpc.server.VehicleOwnerAccountServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VehicleOwnerAccountClientConfig {

	@Value("${grpc.address.name}")
	private String grpcAddressName;

	@Value("${grpc.address.service-port.vehicle-owner}")
	private int grpcVehicleOwnerServicePort;

	@Bean
	public VehicleOwnerAccountServiceGrpc.VehicleOwnerAccountServiceBlockingStub vehicleOwnerAccountServiceBlockingStub() {
		return VehicleOwnerAccountServiceGrpc.newBlockingStub(
				ManagedChannelBuilder.forAddress(grpcAddressName, grpcVehicleOwnerServicePort).usePlaintext().build());
	}

}
