package br.solutis.ticket.dto;

import br.solutis.ticket.entity.Ticket;
import br.solutis.ticket.enums.category.Category;
import br.solutis.ticket.enums.priority.TicketPriority;
import br.solutis.ticket.enums.status.Status;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateTicketRequest(

        @NotBlank
        String title,

        @Column(columnDefinition = "TEXT")
        String description,

        @NotNull
        @Enumerated(EnumType.STRING)
        TicketPriority priority,

        @NotNull
        @Enumerated(EnumType.STRING)
        Status status,

        @NotNull
        @Enumerated(EnumType.STRING)
        Category category,

        @Column(name = "customer_id", nullable = false)
        UUID customerId,

        @Column(name = "customer_id", nullable = false)
        UUID technicianId) {

        public CreateTicketRequest(Ticket ticket){
                this(ticket.getTitle(), ticket.getDescription(), ticket.getPriority(), ticket.getStatus(),ticket.getCategory(), ticket.getCustomerId(),ticket.getTechnicianId());
        }

}
