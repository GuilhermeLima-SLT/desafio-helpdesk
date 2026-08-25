package br.solutis.user.entity;

import br.solutis.user.role.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (active == null) active = true;
        createdAt = LocalDateTime.now();
    }
}

// Adicionando a entitidade da tabela JPA User com os campos: id, nome, email, cargo (ADMIN, TECNICO OU CLIENTE), estado (Ativo ou Inativo) e data de criação