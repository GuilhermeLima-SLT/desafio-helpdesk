package br.solutis.user.dto.response;

import br.solutis.user.role.Role;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(

        @Id
        @NotNull
        @GeneratedValue(strategy = GenerationType.UUID)
        UUID id,

        @NotBlank
        String name,

        @NotBlank
        @Email
        String email,

        @NotNull
        Role role,

        Boolean active,
        LocalDateTime createdAt
) {
}
