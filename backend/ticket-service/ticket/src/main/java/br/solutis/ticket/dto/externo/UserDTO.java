package br.solutis.ticket.dto.externo;

import java.util.UUID;

public record UserDTO(
        UUID id,
        String name,
        String role
) {
}
