package br.solutis.ticket.dto.event;

import java.util.UUID;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TicketEvent(

        @Id
        UUID ticketId,

        @NotBlank
        String eventType,

        @NotBlank
        String title,

        @NotNull
        String status,

        @Id
        UUID technicianId
) {
}
