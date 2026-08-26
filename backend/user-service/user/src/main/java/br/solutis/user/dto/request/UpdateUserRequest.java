package br.solutis.user.dto.request;

import br.solutis.user.role.Role;
import jakarta.persistence.EnumeratedValue;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UpdateUserRequest(

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        UUID id,

        @NotBlank
        String name,

        @NotBlank
        @Email
        String email,

        @EnumeratedValue()
        Role role,

        Boolean active
) {
}
