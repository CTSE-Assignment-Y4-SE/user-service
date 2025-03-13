package com.garage.user_service.core.service;

import com.garage.user_service.core.payload.common.ResponseEntityDto;
import com.garage.user_service.core.payload.request.ForgotPasswordRequestDto;
import com.garage.user_service.core.payload.request.OtpRequestDto;
import com.garage.user_service.core.payload.request.ResetPasswordRequestDto;
import com.garage.user_service.core.payload.request.ServiceManagerRequestDto;
import com.garage.user_service.core.payload.request.SignInRequestDto;
import com.garage.user_service.core.payload.request.SignUpRequestDto;

public interface AuthService {

	ResponseEntityDto createGarageAdmin(SignUpRequestDto createGarageAdminRequestDto);

	ResponseEntityDto signIn(SignInRequestDto signInRequestDto);

	ResponseEntityDto createVehicleOwner(SignUpRequestDto signUpRequestDto);

	ResponseEntityDto resetPassword(ResetPasswordRequestDto resetPasswordRequestDto);

	ResponseEntityDto addServiceManager(ServiceManagerRequestDto serviceManagerRequestDto);

	ResponseEntityDto forgotPassword(ForgotPasswordRequestDto forgotPasswordRequestDto);

	ResponseEntityDto verifyOtp(OtpRequestDto otpRequestDto);

}
