package com.example.cybercert.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import com.example.cybercert.Models.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);
    FullUserDTO toFullDTO(User user);

    List<UserDTO> toDTOs(Collection<User> users);
    List<FullUserDTO> toFullDTOs(Collection<User> users);
    



    default User toEntity(FullUserDTO fullUserDTO) {
        if (fullUserDTO == null) {
            return null;
        }

        User user = new User();
        user.setUsername(fullUserDTO.username());
        user.setEmail(fullUserDTO.email());
        user.setPassword(fullUserDTO.password());
        return user;
    }

}