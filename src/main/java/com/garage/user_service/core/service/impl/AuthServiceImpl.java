package com.garage.user_service.core.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.garage.user_service.core.constant.ApplicationMessages;
import com.garage.user_service.core.constant.ApplicationVariables;
import com.garage.user_service.core.exception.ModuleException;
import com.garage.user_service.core.mapper.MapStructMapper;
import com.garage.user_service.core.model.Otp;
import com.garage.user_service.core.model.User;
import com.garage.user_service.core.payload.common.ResponseEntityDto;
import com.garage.user_service.core.payload.request.EmailRequestDto;
import com.garage.user_service.core.payload.request.ForgotPasswordRequestDto;
import com.garage.user_service.core.payload.request.OtpRequestDto;
import com.garage.user_service.core.payload.request.ResetPasswordRequestDto;
import com.garage.user_service.core.payload.request.ServiceManagerRequestDto;
import com.garage.user_service.core.payload.request.SignInRequestDto;
import com.garage.user_service.core.payload.request.SignUpRequestDto;
import com.garage.user_service.core.payload.response.UserResponseDto;
import com.garage.user_service.core.payload.response.UserSignInResponseDto;
import com.garage.user_service.core.repository.OtpRepository;
import com.garage.user_service.core.repository.UserRepository;
import com.garage.user_service.core.service.AuthService;
import com.garage.user_service.core.service.JwtService;
import com.garage.user_service.core.service.NotificationService;
import com.garage.user_service.core.service.UserService;
import com.garage.user_service.core.service.VehicleOwnerService;
import com.garage.user_service.core.type.EmailType;
import com.garage.user_service.core.type.Role;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

	@NonNull
	private final UserRepository userRepository;

	@NonNull
	private final PasswordEncoder passwordEncoder;

	@NonNull
	private final JwtService jwtService;

	@NonNull
	private final MessageSource messageSource;

	@NonNull
	private final MapStructMapper mapStructMapper;

	@NonNull
	private final VehicleOwnerService vehicleOwnerService;

	@NonNull
	private final UserService userService;

	@NonNull
	private final NotificationService notificationService;

	@NonNull
	private final ObjectMapper objectMapper;

	@NonNull
	private final OtpRepository otpRepository;

	@Override
	@Transactional
	public ResponseEntityDto createGarageAdmin(SignUpRequestDto signUpRequestDto) {
		log.debug("AuthServiceImpl.createGarageAdmin(): execution started");

		List<User> users = userRepository.findAllByRole(Role.GARAGE_ADMIN);
		if (!users.isEmpty()) {
			throw new ModuleException(messageSource.getMessage(ApplicationMessages.ERROR_GARAGE_ADMIN_ALREADY_EXISTS,
					null, Locale.ENGLISH));
		}

		User user = new User();
		user.setEmail(signUpRequestDto.getEmail());
		user.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
		user.setRole(Role.GARAGE_ADMIN);
		String accessToken = jwtService.generateAccessToken(user);

		User savedUser = userRepository.save(user);
		UserSignInResponseDto userSignInResponseDto = mapStructMapper.userToUserSignInResponseDto(savedUser);
		userSignInResponseDto.setAccessToken(accessToken);

		log.debug("AuthServiceImpl.createGarageAdmin(): execution ended");
		return new ResponseEntityDto(true, userSignInResponseDto);
	}

	@Override
	public ResponseEntityDto signIn(SignInRequestDto signInRequestDto) {
		log.debug("AuthServiceImpl.signIn(): execution started");

		Optional<User> optionalUser = userRepository.findByEmail(signInRequestDto.getEmail());
		if (optionalUser.isEmpty()) {
			throw new ModuleException(
					messageSource.getMessage(ApplicationMessages.ERROR_USER_NOT_FOUND, null, Locale.ENGLISH));
		}
		User user = optionalUser.get();

		if (!user.isActive()) {
			throw new ModuleException(
					messageSource.getMessage(ApplicationMessages.ERROR_USER_ACCOUNT_DEACTIVATED, null, Locale.ENGLISH));
		}

		if (!passwordEncoder.matches(signInRequestDto.getPassword(), user.getPassword())) {
			throw new ModuleException(
					messageSource.getMessage(ApplicationMessages.ERROR_INVALID_CURRENT_PASSWORD, null, Locale.ENGLISH));
		}

		String accessToken = jwtService.generateAccessToken(user);

		UserSignInResponseDto userSignInResponseDto = mapStructMapper.userToUserSignInResponseDto(user);
		userSignInResponseDto.setAccessToken(accessToken);

		log.debug("AuthServiceImpl.signIn(): execution ended");
		return new ResponseEntityDto(true, userSignInResponseDto);
	}

	@Override
	@Transactional
	public ResponseEntityDto createVehicleOwner(SignUpRequestDto signUpRequestDto) {
		log.debug("AuthServiceImpl.createVehicleOwner(): execution started");

		Optional<User> optionalUser = userRepository.findByEmail(signUpRequestDto.getEmail());
		if (optionalUser.isPresent()) {
			throw new ModuleException(
					messageSource.getMessage(ApplicationMessages.ERROR_USER_ALREADY_EXISTS, null, Locale.ENGLISH));
		}

		User user = new User();
		user.setEmail(signUpRequestDto.getEmail());
		user.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
		user.setRole(Role.VEHICLE_OWNER);
		String accessToken = jwtService.generateAccessToken(user);

		User savedUser = userRepository.save(user);

		try {
			vehicleOwnerService.createVehicleOwnerAccount(user.getUserId());
		}
		catch (Exception e) {
			log.error("AuthServiceImpl.createVehicleOwner(): execution ended: {}", e.getMessage());
			userRepository.delete(user);
			throw new ModuleException(messageSource
				.getMessage(ApplicationMessages.ERROR_VEHICLE_OWNER_CANNOT_BE_CREATED, null, Locale.ENGLISH));
		}

		UserSignInResponseDto userSignInResponseDto = mapStructMapper.userToUserSignInResponseDto(savedUser);
		userSignInResponseDto.setAccessToken(accessToken);

		log.debug("AuthServiceImpl.createVehicleOwner(): execution ended");
		return new ResponseEntityDto(true, userSignInResponseDto);
	}

	@Override
	@Transactional
	public ResponseEntityDto resetPassword(ResetPasswordRequestDto resetPasswordRequestDto) {
		log.debug("UserServiceImpl.resetPassword(): execution started");

		User user = userService.getAuthenticatedUser();

		if (!passwordEncoder.matches(resetPasswordRequestDto.getCurrentPassword(), user.getPassword())) {
			throw new ModuleException(
					messageSource.getMessage(ApplicationMessages.ERROR_INVALID_CURRENT_PASSWORD, null, Locale.ENGLISH));
		}

		String encodedPassword = passwordEncoder.encode(resetPasswordRequestDto.getNewPassword());
		user.setPassword(encodedPassword);

		userRepository.save(user);

		log.debug("UserServiceImpl.resetPassword(): execution ended");
		return new ResponseEntityDto(true, null);
	}

	@Override
	@Transactional
	public ResponseEntityDto addServiceManager(ServiceManagerRequestDto serviceManagerRequestDto) {
		log.debug("UserServiceImpl.addServiceManager(): execution started");

		Optional<User> optionalUser = userRepository.findByEmail(serviceManagerRequestDto.getEmail());
		if (optionalUser.isPresent()) {
			throw new ModuleException(
					messageSource.getMessage(ApplicationMessages.ERROR_USER_ALREADY_EXISTS, null, Locale.ENGLISH));
		}

		User user = new User();

		String randomPassword = generateRandomPassword();

		user.setEmail(serviceManagerRequestDto.getEmail());
		user.setPassword(passwordEncoder.encode(randomPassword));
		user.setRole(Role.SERVICE_MANAGER);

		User savedUser = userRepository.save(user);

		EmailRequestDto emailRequestDto = new EmailRequestDto();
		emailRequestDto.setEmail(user.getEmail());
		emailRequestDto.setBody(randomPassword);
		emailRequestDto.setEmailType(EmailType.SERVICE_MANAGER_TEMP_PASSWORD);

		notificationService.sendEmailAsync(emailRequestDto);

		UserResponseDto userResponseDto = mapStructMapper.userToUserResponseDto(savedUser);

		log.debug("UserServiceImpl.addServiceManager(): execution ended");
		return new ResponseEntityDto(true, userResponseDto);
	}

	@Override
	@Transactional
	public ResponseEntityDto forgotPassword(ForgotPasswordRequestDto forgotPasswordRequestDto) {
		log.debug("UserServiceImpl.forgotPassword(): execution started");

		Optional<User> optionalUser = userRepository.findByEmail(forgotPasswordRequestDto.getEmail());
		if (optionalUser.isEmpty()) {
			throw new ModuleException(
					messageSource.getMessage(ApplicationMessages.ERROR_USER_NOT_FOUND, null, Locale.ENGLISH));
		}
		User user = optionalUser.get();

		Otp otp = new Otp();
		String otpCode = generateOtp();

		otp.setEmail(forgotPasswordRequestDto.getEmail());
		otp.setOtpCode(otpCode);
		otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
		otp.setUsed(false);

		otpRepository.save(otp);

		markExpiredOtpsAsUsed(user.getEmail());

		EmailRequestDto emailRequestDto = new EmailRequestDto();
		emailRequestDto.setEmail(user.getEmail());
		emailRequestDto.setBody(otpCode);
		emailRequestDto.setEmailType(EmailType.USER_FORGOT_PASSWORD);

		notificationService.sendEmailAsync(emailRequestDto);

		log.debug("UserServiceImpl.forgotPassword(): execution ended");
		return new ResponseEntityDto(true, null);
	}

	@Override
	@Transactional
	public ResponseEntityDto verifyOtp(OtpRequestDto otpRequestDto) {
		log.debug("UserServiceImpl.verifyOtp(): execution started");

		Optional<User> optionalUser = userRepository.findByEmail(otpRequestDto.getEmail());
		if (optionalUser.isEmpty()) {
			throw new ModuleException(
					messageSource.getMessage(ApplicationMessages.ERROR_USER_NOT_FOUND, null, Locale.ENGLISH));
		}
		User user = optionalUser.get();

		markExpiredOtpsAsUsed(user.getEmail());

		Optional<Otp> optionalOtp = otpRepository.findByEmailAndUsedAndExpiresAtAfter(otpRequestDto.getEmail(), false,
				LocalDateTime.now());

		if (optionalOtp.isEmpty()) {
			throw new ModuleException(
					messageSource.getMessage(ApplicationMessages.ERROR_USER_NOT_FOUND, null, Locale.ENGLISH));
		}
		Otp otp = optionalOtp.get();

		if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new ModuleException(
					messageSource.getMessage(ApplicationMessages.ERROR_OTP_EXPIRED, null, Locale.ENGLISH));
		}

		if (!otp.getOtpCode().equals(otpRequestDto.getOtpCode())) {
			throw new ModuleException(
					messageSource.getMessage(ApplicationMessages.ERROR_INVALID_OTP, null, Locale.ENGLISH));
		}

		otp.setUsed(true);
		otpRepository.save(otp);

		String accessToken = jwtService.generateAccessToken(user);

		UserSignInResponseDto userSignInResponseDto = mapStructMapper.userToUserSignInResponseDto(user);
		userSignInResponseDto.setAccessToken(accessToken);

		log.debug("UserServiceImpl.verifyOtp(): execution ended");
		return new ResponseEntityDto(true, userSignInResponseDto);
	}

	public static String generateRandomPassword() {
		SecureRandom random = new SecureRandom();
		StringBuilder password = new StringBuilder(ApplicationVariables.PASSWORD_LENGTH);

		for (int i = 0; i < ApplicationVariables.PASSWORD_LENGTH; i++) {
			int index = random.nextInt(ApplicationVariables.CHARACTERS.length());
			password.append(ApplicationVariables.CHARACTERS.charAt(index));
		}

		return password.toString();
	}

	private String generateOtp() {
		SecureRandom random = new SecureRandom();
		int otp = 100000 + random.nextInt(900000);
		return String.valueOf(otp);
	}

	private void markExpiredOtpsAsUsed(String email) {
		otpRepository.findAllByEmailAndUsedFalseAndExpiresAtBefore(email, LocalDateTime.now()).forEach(otp -> {
			otp.setUsed(true);
			otpRepository.save(otp);
		});

		List<Otp> unusedOtps = otpRepository.findAllByEmailAndUsedFalse(email);
		if (unusedOtps.size() > 1) {
			unusedOtps.stream()
				.sorted((otp1, otp2) -> otp2.getCreatedAt().compareTo(otp1.getCreatedAt()))
				.skip(1)
				.forEach(otp -> {
					otp.setUsed(true);
					otpRepository.save(otp);
					log.info("Marked duplicate OTP as used: {}", otp.getOtpCode());
				});
		}
	}

}
