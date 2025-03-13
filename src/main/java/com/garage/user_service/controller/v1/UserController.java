package com.garage.user_service.controller.v1;

import com.garage.user_service.core.payload.common.ResponseEntityDto;
import com.garage.user_service.core.service.UserService;
import com.garage.user_service.core.type.Role;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

	@NonNull
	private final UserService userService;

	@GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseEntityDto> getProfile() {
		ResponseEntityDto response = userService.getCurrentUser();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('GARAGE_ADMIN')")
	@GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseEntityDto> getUsers(@RequestParam List<Role> roles) {
		ResponseEntityDto response = userService.getUsersByRoles(roles);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PatchMapping(value = "/deactivate/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseEntityDto> deactivateUser(@PathVariable Long userId) {
		ResponseEntityDto response = userService.deactivateUserAccount(userId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('GARAGE_ADMIN')")
	@PatchMapping(value = "/activate/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseEntityDto> activateUser(@PathVariable Long userId) {
		ResponseEntityDto response = userService.activateUser(userId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
