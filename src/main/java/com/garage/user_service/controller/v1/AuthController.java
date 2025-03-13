package com.garage.user_service.controller.v1;

import com.garage.user_service.core.payload.common.ResponseEntityDto;
import com.garage.user_service.core.payload.request.ForgotPasswordRequestDto;
import com.garage.user_service.core.payload.request.OtpRequestDto;
import com.garage.user_service.core.payload.request.ResetPasswordRequestDto;
import com.garage.user_service.core.payload.request.ServiceManagerRequestDto;
import com.garage.user_service.core.payload.request.SignInRequestDto;
import com.garage.user_service.core.payload.request.SignUpRequestDto;
import com.garage.user_service.core.service.AuthService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	@NonNull
	private final AuthService authService;

	@PostMapping(value = "/admin/sign-up", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseEntityDto> createGarageAdmin(@RequestBody SignUpRequestDto signUpRequestDto) {
		ResponseEntityDto response = authService.createGarageAdmin(signUpRequestDto);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping(value = "/sign-in", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseEntityDto> signIn(@RequestBody SignInRequestDto signInRequestDto) {
		ResponseEntityDto response = authService.signIn(signInRequestDto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/sign-up", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseEntityDto> createVehicleOwner(@RequestBody SignUpRequestDto signUpRequestDto) {
		ResponseEntityDto response = authService.createVehicleOwner(signUpRequestDto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PatchMapping(value = "/reset/password", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseEntityDto> resetPassword(
			@RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
		ResponseEntityDto response = authService.resetPassword(resetPasswordRequestDto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('GARAGE_ADMIN')")
	@PostMapping(value = "/admin/service-manager", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseEntityDto> addServiceManager(
			@RequestBody ServiceManagerRequestDto serviceManagerRequestDto) {
		ResponseEntityDto response = authService.addServiceManager(serviceManagerRequestDto);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping(value = "/forgot-password", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseEntityDto> forgotPassword(
			@RequestBody ForgotPasswordRequestDto forgotPasswordRequestDto) {
		ResponseEntityDto response = authService.forgotPassword(forgotPasswordRequestDto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/verify/otp", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseEntityDto> verifyOtp(@Valid @RequestBody OtpRequestDto otpRequestDto) {
		ResponseEntityDto response = authService.verifyOtp(otpRequestDto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
