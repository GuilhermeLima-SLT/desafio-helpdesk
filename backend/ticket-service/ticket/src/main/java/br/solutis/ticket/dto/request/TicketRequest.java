package br.solutis.ticket.dto.request;

import br.solutis.ticket.entity.Ticket;
import br.solutis.ticket.enums.category.Category;
import br.solutis.ticket.enums.priority.TicketPriority;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TicketRequest(

        @NotBlank(message = " !! Titulo é um campo obrigatório !! ")
        String title,

        @Column(columnDefinition = "TEXT")
        String description,

        @NotNull(message = " !! Prioridade é um campo obrigatório !! ")
        @Enumerated(EnumType.STRING)
        TicketPriority priority,

        @NotNull(message = " !! Title é um campo obrigatório !! ")
        @Enumerated(EnumType.STRING)
        Category category,

        @Column(name = "customer_id", nullable = false)
        UUID customerId
){
        public TicketRequest(Ticket ticket){
                this(ticket.getTitle(), ticket.getDescription(), ticket.getPriority(),ticket.getCategory(), ticket.getCustomerId());
        }
}
