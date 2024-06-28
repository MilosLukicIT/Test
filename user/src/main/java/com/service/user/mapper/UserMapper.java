package com.service.user.mapper;


import com.service.user.model.User;
import com.service.user.model.dto.UserCreateDto;
import com.service.user.model.dto.UserViewDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {


    @Mapping(target = "userRole", expression = "java(com.service.user.model.enums.UserRole.valueOf(dto.getUserRole()))")
    User toUserFromCreate(UserCreateDto dto);
    @Mapping(target = "userRole", expression = "java(user.getUserRole().name())")
    UserViewDto toViewDtoFromUser(User user);
    @Mapping(target = "userRole", expression = "java(user.getUserRole().name())")
    List<UserViewDto> toViewPage(Page<User> users);
}
