package com.garage.user_service.core.service;

import com.garage.user_service.core.model.User;
import com.garage.user_service.core.payload.common.ResponseEntityDto;
import com.garage.user_service.core.type.Role;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService {

	UserDetailsService getUserDetailsService();

	ResponseEntityDto getCurrentUser();

	User getAuthenticatedUser();

	ResponseEntityDto getUsersByRoles(List<Role> roles);

	ResponseEntityDto deactivateUserAccount(Long userId);

	ResponseEntityDto activateUser(Long userId);

}
