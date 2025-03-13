package com.garage.user_service.core.component;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

@Slf4j
@Component
public class JwtGrpcAuthenticationReader implements GrpcAuthenticationReader {

	@Nullable
	@Override
	public Authentication readAuthentication(ServerCall<?, ?> call, Metadata headers) throws AuthenticationException {
		log.debug("JwtGrpcAuthenticationReader.readAuthentication(): execution started");

		log.debug("JwtGrpcAuthenticationReader.readAuthentication(): execution ended");
		return null;
	}

}
