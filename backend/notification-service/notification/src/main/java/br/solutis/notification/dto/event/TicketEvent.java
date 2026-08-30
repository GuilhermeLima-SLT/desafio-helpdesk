package br.solutis.notification.dto.event;

import java.util.UUID;

public record TicketEvent(

        UUID ticketId,
        String eventType,
        String title,
        String status,
        UUID technicianId
) {}
