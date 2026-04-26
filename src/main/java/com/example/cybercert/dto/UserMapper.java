package com.example.cybercert.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import com.example.cybercert.Models.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    List<UserDTO> toDTOs(Collection<User> users);

}