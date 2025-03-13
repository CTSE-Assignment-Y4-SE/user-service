package com.garage.user_service.core.service.impl;

import com.garage.user_service.core.constant.ApplicationMessages;
import com.garage.user_service.core.exception.ModuleException;
import com.garage.user_service.core.mapper.MapStructMapper;
import com.garage.user_service.core.model.User;
import com.garage.user_service.core.payload.common.ResponseEntityDto;
import com.garage.user_service.core.payload.response.UserResponseDto;
import com.garage.user_service.core.payload.response.VehicleOwnerResponseDto;
import com.garage.user_service.core.repository.UserRepository;
import com.garage.user_service.core.service.UserService;
import com.garage.user_service.core.service.VehicleOwnerService;
import com.garage.user_service.core.type.Role;
import com.garage.vehicle_owner_service.core.grpc.server.VehicleOwnerAccountCreateResponse;
import com.garage.vehicle_owner_service.core.grpc.server.VehicleOwnerAccountListResponse;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

	@NonNull
	private final UserRepository userRepository;

	@NonNull
	private final MessageSource messageSource;

	@NonNull
	private final MapStructMapper mapStructMapper;

	@NonNull
	private final VehicleOwnerService vehicleOwnerService;

	@Override
	public UserDetailsService getUserDetailsService() {
		log.debug("UserServiceImpl.getUserDetailsService(): execution started and ended");
		return username -> (UserDetails) userRepository.findByEmail(username)
			.orElseThrow(() -> new ModuleException(
					messageSource.getMessage(ApplicationMessages.ERROR_USER_NOT_FOUND, null, Locale.ENGLISH)));
	}

	@Override
	public ResponseEntityDto getCurrentUser() {
		log.debug("UserServiceImpl.getCurrentUser(): execution started");

		User user = getAuthenticatedUser();

		UserResponseDto userResponseDto = mapStructMapper.userToUserResponseDto(user);

		log.debug("UserServiceImpl.getCurrentUser(): execution ended");
		return new ResponseEntityDto(true, userResponseDto);
	}

	@Override
	public User getAuthenticatedUser() {
		log.debug("UserServiceImpl.getAuthenticatedUser(): execution started");

		User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Optional<User> user = userRepository.findById(currentUser.getUserId());
		if (user.isEmpty()) {
			throw new ModuleException(
					messageSource.getMessage(ApplicationMessages.ERROR_USER_NOT_FOUND, null, Locale.ENGLISH));
		}

		log.debug("UserServiceImpl.getAuthenticatedUser(): execution ended");
		return user.get();
	}

	@Override
	public ResponseEntityDto getUsersByRoles(List<Role> roles) {
		log.debug("UserServiceImpl.getUsersByRoles(): execution started");

		if (roles == null || roles.isEmpty()) {
			roles = List.of(Role.GARAGE_ADMIN, Role.SERVICE_MANAGER, Role.VEHICLE_OWNER);
		}

		List<User> users = userRepository.findAllByRoleIn(roles);

		Map<Long, VehicleOwnerResponseDto> vehicleOwnerDataMap = new HashMap<>();

		if (roles.contains(Role.VEHICLE_OWNER)) {
			VehicleOwnerAccountListResponse vehicleOwnerAccountListResponse = vehicleOwnerService.GetAllVehicleOwners();

			for (VehicleOwnerAccountCreateResponse ownerResponse : vehicleOwnerAccountListResponse
				.getVehicleOwnersList()) {
				VehicleOwnerResponseDto vehicleOwnerDto = new VehicleOwnerResponseDto();
				vehicleOwnerDto.setVehicleOwnerId(ownerResponse.getVehicleOwnerId());
				vehicleOwnerDto.setFirstName(ownerResponse.getFirstName());
				vehicleOwnerDto.setLastName(ownerResponse.getLastName());
				vehicleOwnerDto.setPhoneNumber(ownerResponse.getPhoneNumber());

				vehicleOwnerDataMap.put(ownerResponse.getUserId(), vehicleOwnerDto);
			}
		}

		List<UserResponseDto> userResponseDtos = users.stream().map(user -> {
			UserResponseDto userDto = new UserResponseDto();
			userDto.setUserId(user.getUserId());
			userDto.setEmail(user.getEmail());
			userDto.setRole(user.getRole());
			userDto.setActive(user.isActive());

			if (user.getRole() == Role.VEHICLE_OWNER && vehicleOwnerDataMap.containsKey(user.getUserId())) {
				userDto.setVehicleOwnerResponseDto(vehicleOwnerDataMap.get(user.getUserId()));
			}

			return userDto;
		}).toList();

		log.debug("UserServiceImpl.getUsersByRoles(): execution ended");
		return new ResponseEntityDto(true, userResponseDtos);
	}

	@Override
	@Transactional
	public ResponseEntityDto deactivateUserAccount(Long userId) {
		log.debug("UserServiceImpl.deactivateUserAccount(): execution started");

		User currentUser = getAuthenticatedUser();

		if (!currentUser.getUserId().equals(userId) && !currentUser.getRole().equals(Role.GARAGE_ADMIN)) {
			throw new ModuleException(
					messageSource.getMessage(ApplicationMessages.ERROR_PERMISSION_DENIED, null, Locale.ENGLISH));
		}

		Optional<User> optionalUser = userRepository.findById(userId);
		if (optionalUser.isEmpty()) {
			throw new ModuleException(
					messageSource.getMessage(ApplicationMessages.ERROR_USER_NOT_FOUND, null, Locale.ENGLISH));
		}
		User user = optionalUser.get();

		user.setActive(false);

		userRepository.save(user);

		log.debug("UserServiceImpl.deactivateUserAccount(): execution ended");
		return new ResponseEntityDto(true, null);
	}

	@Override
	@Transactional
	public ResponseEntityDto activateUser(Long userId) {
		log.debug("UserServiceImpl.activateUser(): execution started");

		Optional<User> optionalUser = userRepository.findById(userId);
		if (optionalUser.isEmpty()) {
			throw new ModuleException(
					messageSource.getMessage(ApplicationMessages.ERROR_USER_NOT_FOUND, null, Locale.ENGLISH));
		}
		User user = optionalUser.get();

		user.setActive(true);

		userRepository.save(user);

		log.debug("UserServiceImpl.activateUser(): execution ended");
		return new ResponseEntityDto(true, null);
	}

}
