package br.solutis.ticket.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AtribuirTecnicoRequest(
        @NotNull(message = " !! TechnicianId é obrigatório !! ")
        UUID technicianId
) {
}
