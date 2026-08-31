package br.solutis.ticket.mapper;

import br.solutis.ticket.dto.request.TicketRequest;
import br.solutis.ticket.dto.response.TicketResponse;
import br.solutis.ticket.entity.Ticket;

public class TicketMapper {

    public static Ticket toEntity(TicketRequest dto) {
        Ticket t = new Ticket();
        t.setTitle(dto.title());
        t.setDescription(dto.description());
        t.setPriority(dto.priority());
        t.setCategory(dto.category());
        t.setCustomerId(dto.customerId());
        return t;
    }

    public static TicketResponse toResponse(Ticket t) {
        return new TicketResponse(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.getStatus(),
                t.getPriority(),
                t.getCategory(),
                t.getCustomerId(),
                t.getTechnicianId(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}