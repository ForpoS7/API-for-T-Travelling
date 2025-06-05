package ru.itis.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.itis.api.dto.UpdateUserDto;
import ru.itis.api.dto.UserDto;
import ru.itis.api.entity.User;
import ru.itis.api.exception.PasswordDoNotMatchException;
import ru.itis.api.exception.UserAlreadyExistException;
import ru.itis.api.exception.UserNotFoundException;
import ru.itis.api.mapper.UserMapper;
import ru.itis.api.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDto getUserByPhoneNumber(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber).
                orElseThrow(() -> new UsernameNotFoundException("User not found with phone number: " + phoneNumber));
        return userMapper.mapToUserDto(user);
    }

    public UpdateUserDto updateUser(UpdateUserDto updateUserDto, String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber).
                orElseThrow(() -> new UserNotFoundException("User not found with phone number: " + phoneNumber));
        if (userRepository.existsByPhoneNumber(updateUserDto.getPhoneNumber())){
            throw new UserAlreadyExistException("Phone number already exist");
        }
        user.setPhoneNumber(updateUserDto.getPhoneNumber());
        user.setFirstName(updateUserDto.getFirstName());
        user.setLastName(updateUserDto.getLastName());
        userRepository.save(user);
        return updateUserDto;
    }

    public void updateDeviceToken(String deviceToken, Long userId) {
        userRepository.updateDeviceTokenById(userId, deviceToken);
    }
}