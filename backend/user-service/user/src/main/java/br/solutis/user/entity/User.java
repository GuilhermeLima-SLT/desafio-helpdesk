package br.solutis.user.entity;

import br.solutis.user.role.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    @NotBlank
    private String email;

    @NotBlank
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;
}

// Adicionando a entitidade da tabela JPA User com os campos: id, nome, email e cargo (ADMIN, TECNICO OU CLIENTE)