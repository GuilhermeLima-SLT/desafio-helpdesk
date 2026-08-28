package br.solutis.ticket.dto.request;

import br.solutis.ticket.entity.Ticket;
import br.solutis.ticket.enums.Category;
import br.solutis.ticket.enums.TicketPriority;
import br.solutis.ticket.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TicketRequest(

        @NotBlank(message = " !! Titulo é um campo obrigatório !! ")
        String title,

        @NotBlank(message = " !! Descricao é um campo obrigatório !! ")
        @Column(columnDefinition = "TEXT")
        String description,

        @NotNull(message = " !! Prioridade é um campo obrigatório !! ")
        @Enumerated(EnumType.STRING)
        TicketPriority priority,

        @Enumerated(EnumType.STRING)
        Status status,

        @NotNull(message = " !! Categoria é um campo obrigatório !! ")
        @Enumerated(EnumType.STRING)
        Category category,

        @Column(name = "customer_id", nullable = false)
        UUID customerId
){
        public TicketRequest(Ticket ticket){
                this(ticket.getTitle(), ticket.getDescription(), ticket.getPriority(),ticket.getStatus(),ticket.getCategory(), ticket.getCustomerId());
        }
}
