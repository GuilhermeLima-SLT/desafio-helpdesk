package br.solutis.ticket.dto.response;

import br.solutis.ticket.enums.category.Category;
import br.solutis.ticket.enums.priority.TicketPriority;
import br.solutis.ticket.enums.status.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TicketResponse(

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        UUID id,

        @NotBlank
        String title,

        @Column(columnDefinition = "TEXT")
        String description,

        @NotNull
        @Column(nullable = false)
        @Enumerated(EnumType.STRING)
        Status status,

        @NotNull
        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        TicketPriority priority,

        @NotNull
        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        Category category,

        @Column(name = "customer_id", nullable = false)
        UUID customerId,

        @Column(name = "technician_id")
        UUID technicianId
) {
}
