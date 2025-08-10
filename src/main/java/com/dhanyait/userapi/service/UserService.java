package com.dhanyait.userapi.service;

import com.dhanyait.userapi.dto.UserDto;
import com.dhanyait.userapi.entity.User;
import com.dhanyait.userapi.exception.UserAlreadyExistsException;
import com.dhanyait.userapi.exception.UserNotFoundException;
import com.dhanyait.userapi.mapper.UserMapper;
import com.dhanyait.userapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        logger.debug("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        logger.debug("Fetching all users");
        List<User> users = userRepository.findAll();
        return userMapper.toDtoList(users);
    }

    public UserDto createUser(UserDto userDto) {
        logger.debug("Creating new user with email: {}", userDto.getEmail());

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistsException("User already exists with email: " + userDto.getEmail());
        }

        User user = userMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);

        logger.info("Successfully created user with id: {}", savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        logger.debug("Updating user with id: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        // Check if email is being changed and if it already exists
        if (!existingUser.getEmail().equals(userDto.getEmail()) &&
                userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistsException("User already exists with email: " + userDto.getEmail());
        }

        userMapper.updateEntityFromDto(userDto, existingUser);
        User updatedUser = userRepository.save(existingUser);

        logger.info("Successfully updated user with id: {}", id);
        return userMapper.toDto(updatedUser);
    }

    public void deleteUser(Long id) {
        logger.debug("Deleting user with id: {}", id);

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
        logger.info("Successfully deleted user with id: {}", id);
    }
}
