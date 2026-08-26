package br.solutis.user.controller;

import br.solutis.user.dto.request.UpdateUserRequest;
import br.solutis.user.dto.request.UserRequest;
import br.solutis.user.dto.response.UserResponse;
import br.solutis.user.entity.User;
import br.solutis.user.mapper.UserMapper;
import br.solutis.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    // Corrigido conexão direta com entity usando DTO e Mapper e retorno de HTTP para '201 Created' com uso do status(HttpStatus)...
    @Operation(summary = "Criar novo usuário")
    @PostMapping
    public ResponseEntity<UserRequest> createUser(@Valid @RequestBody UserRequest request) {
        User user = UserMapper.toEntity(request);

        User saved = userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UserMapper.toResponse(saved));
    }

    //Corrigido conexão direta com entity usando DTO e Mapper
    @Operation(summary = "Listar todos os usuários (Incluindo inativos)")
    @GetMapping("/all")
    public ResponseEntity<List<UserRequest>> listUsers() {
        List<UserRequest> response = userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar todos os usuários ativos")
    @GetMapping
    public ResponseEntity<List<UserRequest>> listAllActive() {
        List<UserRequest> response = userRepository.findByActiveTrue()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar usuário por ID")
//    @GetMapping("/{id}")
//    public ResponseEntity<Optional<User>> getUserById(@PathVariable UUID id) {
//        return ResponseEntity.ok(userRepository.findById(id));
//    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {

        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Atualizar usuário por ID")
    @PutMapping("/{id}")
    //ResponseEntity não aceitando <UpdateUserRequest>
    public ResponseEntity<UserRequest> updateUser(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateUserRequest request) {

        return userRepository.findById(id)
                .map(user -> {
                    user.setName(request.name());
                    user.setEmail(request.email());
                    user.setRole(request.role());

                    User updated = userRepository.save(user);
                    return ResponseEntity.ok(UserMapper.toResponse(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Inativar usuário (Exclusão lógica)")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<User> deactivateUser(@PathVariable UUID id) {

        return userRepository.findById(id)
                .map(user -> {
                    user.setActive(false);
                    return ResponseEntity.ok(userRepository.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Excluir (inativar) usuário")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable UUID id) {

        return userRepository.findById(id)
                .map(user -> {
                    user.setActive(false);
                    userRepository.save(user);
                    return ResponseEntity.noContent().build(); // 204
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
