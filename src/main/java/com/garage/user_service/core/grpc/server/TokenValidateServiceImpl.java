package com.garage.user_service.core.grpc.server;

import com.garage.user_service.core.service.JwtService;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Map;

@GrpcService
@AllArgsConstructor
public class TokenValidateServiceImpl extends TokenValidateServiceGrpc.TokenValidateServiceImplBase {

	@NonNull
	private final JwtService jwtService;

	@Override
	public void validateToken(TokenValidateRequest tokenValidateRequest,
			StreamObserver<TokenValidateResponse> responseStreamObserver) {

		boolean tokenValidStatus = jwtService.validateToken(tokenValidateRequest.getToken());
		Map<String, String> claims = jwtService.getAllClaims(tokenValidateRequest.getToken());

		TokenValidateResponse tokenValidateResponse = TokenValidateResponse.newBuilder()
			.setIsValid(tokenValidStatus)
			.putAllClaims(claims)
			.build();
		responseStreamObserver.onNext(tokenValidateResponse);
		responseStreamObserver.onCompleted();
	}

}
