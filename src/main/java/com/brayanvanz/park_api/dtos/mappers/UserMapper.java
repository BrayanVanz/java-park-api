package com.brayanvanz.park_api.dtos.mappers;

import org.modelmapper.ModelMapper;

import com.brayanvanz.park_api.dtos.UserResponseDto;
import com.brayanvanz.park_api.dtos.UserSaveDto;
import com.brayanvanz.park_api.entities.User;

public class UserMapper {

    public static User toUser(UserSaveDto userSaveDto) {
        return new ModelMapper().map(userSaveDto, User.class);
    }

    public static UserResponseDto toDto(User user) {
        return new ModelMapper().map(user, UserResponseDto.class);
    }
}
