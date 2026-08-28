package br.solutis.user.service;

import br.solutis.user.dto.request.UpdateUserRequest;
import br.solutis.user.dto.request.UserRequest;
import br.solutis.user.dto.response.UserResponse;
import br.solutis.user.entity.User;
import br.solutis.user.mapper.UserMapper;
import br.solutis.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public UserResponse createUser(@Valid UserRequest response) {
        User user = UserMapper.toEntity(response);
        user.setActive(true); // Garante que nasce ativo, caso não venha no mapper
        User saved = userRepository.save(user);
        return UserMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listAllActive() {
        return userRepository.findByActiveTrue()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        return UserMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateUser(@Valid UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(request.role());

        User updated = userRepository.save(user);
        return UserMapper.toResponse(updated);
    }

    @Transactional
    public UserResponse deactivateUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        user.setActive(false);
        User updated = userRepository.save(user);
        return UserMapper.toResponse(updated);
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        user.setActive(false);
        userRepository.save(user);
    }
}
