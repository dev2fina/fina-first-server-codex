package net.fina.first.service;

import java.util.List;
import net.fina.first.dto.UserDto;
import net.fina.first.exception.NotFoundException;
import net.fina.first.mapper.UserMapper;
import net.fina.first.model.User;
import net.fina.first.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    public UserDto findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toDto(user);
    }

    public UserDto create(UserDto dto, String rawPassword) {
        User user = userMapper.toEntity(dto);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    public UserDto update(Long id, UserDto dto) {
        User existing = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        existing.setEmail(dto.getEmail());
        existing.setActive(dto.isActive());
        existing.setRoles(existing.getRoles());
        User saved = userRepository.save(existing);
        return userMapper.toDto(saved);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }
}
