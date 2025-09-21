package com.serb.miniDoodle.service;

import com.serb.miniDoodle.dto.UserRequestDTO;
import com.serb.miniDoodle.dto.UserResponseDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO request);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(UUID id);
}
