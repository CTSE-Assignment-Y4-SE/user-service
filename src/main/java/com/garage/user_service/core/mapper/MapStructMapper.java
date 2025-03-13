package com.garage.user_service.core.mapper;

import com.garage.user_service.core.model.User;
import com.garage.user_service.core.payload.response.UserResponseDto;
import com.garage.user_service.core.payload.response.UserSignInResponseDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MapStructMapper {

	UserSignInResponseDto userToUserSignInResponseDto(User user);

	UserResponseDto userToUserResponseDto(User user);

	List<UserResponseDto> userListToUserResponseDtoList(List<User> users);

}
