package br.solutis.user.dto.request;

import br.solutis.user.role.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserRequest(

        @NotBlank
        String name,

        @NotBlank
        @Email
        String email,

        @NotNull
        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        Role role
) {
}
