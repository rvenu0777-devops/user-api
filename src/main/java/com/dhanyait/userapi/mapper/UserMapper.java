package com.dhanyait.userapi.mapper;


import com.dhanyait.userapi.dto.UserDto;
import com.dhanyait.userapi.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userDto);

    List<UserDto> toDtoList(List<User> users);

    void updateEntityFromDto(UserDto userDto, @MappingTarget User user);
}
