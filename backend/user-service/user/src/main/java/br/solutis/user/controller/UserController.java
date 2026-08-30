package br.solutis.user.controller;

import br.solutis.user.dto.request.UpdateUserRequest;
import br.solutis.user.dto.request.UserRequest;
import br.solutis.user.dto.response.UserResponse;
import br.solutis.user.service.UserService;
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
    private UserService userService;

    // Corrigido conexão direta com entity usando DTO e Mapper e retorno de HTTP para '201 Created' com uso do status(HttpStatus)...
    // Segunda correcao: Logica de processamento completa encapsulada em repositorio Service para adicionar mais uma camada de protecao e organizacao do codigo
    @Operation(summary = "Criar novo usuário")
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) { // Alterado para UserRequest
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar todos os usuários (Incluindo inativos)")
    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> listUsers() {
        List<UserResponse> response = userService.listUsers();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar todos os usuários ativos")
    @GetMapping
    public ResponseEntity<List<UserResponse>> listAllActive() {
        List<UserResponse> response = userService.listAllActive();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar usuário por ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualizar usuário por ID")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @Valid
            @PathVariable UUID id,
            @RequestBody @Valid UpdateUserRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Inativar usuário (Exclusão lógica)")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserResponse> deactivateUser(@Valid @PathVariable UUID id) {
        UserResponse response = userService.deactivateUser(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Inativar usuário (Excluir da base de dados)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
