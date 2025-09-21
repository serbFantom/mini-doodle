package com.serb.miniDoodle.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.serb.miniDoodle.dto.UserRequestDTO;
import com.serb.miniDoodle.dto.UserResponseDTO;
import com.serb.miniDoodle.model.User;
import com.serb.miniDoodle.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser() {
        UserRequestDTO request = new UserRequestDTO();
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setAddress("123 Main St");

        User userToSave = new User();
        // We expect the service to assign the ID and timestamps, so we mock the saveAndFlush result
        // For simplicity, ignoring equals and using ArgumentCaptor to capture the saved user
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO response = userService.createUser(request);

        assertNotNull(response.getId());
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(request.getAddress(), response.getAddress());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());

        verify(userRepository, times(1)).saveAndFlush(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(request.getName(), savedUser.getName());
        assertEquals(request.getEmail(), savedUser.getEmail());
        assertEquals(request.getAddress(), savedUser.getAddress());
        assertNotNull(savedUser.getCreatedAt());
        assertNotNull(savedUser.getUpdatedAt());
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setName("Alice");
        user1.setEmail("alice@example.com");
        user1.setAddress("Address 1");
        user1.setCreatedAt(Instant.now());
        user1.setUpdatedAt(Instant.now());

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setName("Bob");
        user2.setEmail("bob@example.com");
        user2.setAddress("Address 2");
        user2.setCreatedAt(Instant.now());
        user2.setUpdatedAt(Instant.now());

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserResponseDTO> users = userService.getAllUsers();

        assertEquals(2, users.size());
        assertEquals(user1.getId(), users.get(0).getId());
        assertEquals(user2.getId(), users.get(1).getId());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserByIdUserExists() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setName("Charlie");
        user.setEmail("charlie@example.com");
        user.setAddress("Address 3");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponseDTO response = userService.getUserById(userId);

        assertEquals(userId, response.getId());
        assertEquals(user.getName(), response.getName());
        assertEquals(user.getEmail(), response.getEmail());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
     void testGetUserByIdUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.getUserById(userId);
        });

        assertTrue(exception.getMessage().contains("User not found with id"));
        verify(userRepository, times(1)).findById(userId);
    }

}