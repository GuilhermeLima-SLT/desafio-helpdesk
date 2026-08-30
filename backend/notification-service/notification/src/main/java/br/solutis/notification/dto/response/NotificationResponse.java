package br.solutis.notification.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID ticketId,
        String type,
        String message,
        LocalDateTime createdAt
) {
}
