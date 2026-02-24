package com.company.seatallocation.service;

import com.company.seatallocation.dto.UserDto;
import com.company.seatallocation.entity.User;
import com.company.seatallocation.exception.BusinessException;
import com.company.seatallocation.mapper.UserMapper;
import com.company.seatallocation.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto createUser(@Valid UserDto userDto) {
        userRepository.findByEmail(userDto.getEmail())
                .ifPresent(u -> {
                    throw new BusinessException("User with this email already exists");
                });

        User user = userMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found"));
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto updateUser(String id, @Valid UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found"));

        if (!user.getEmail().equals(userDto.getEmail())) {
            userRepository.findByEmail(userDto.getEmail())
                    .ifPresent(u -> {
                        throw new BusinessException("User with this email already exists");
                    });
        }

        userMapper.updateEntityFromDto(userDto, user);
        
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    @Transactional
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException("User not found");
        }
        userRepository.deleteById(id);
    }
}
