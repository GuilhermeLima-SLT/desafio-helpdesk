package br.solutis.ticket.repository;

import br.solutis.ticket.entity.Ticket;
import br.solutis.ticket.enums.status.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    List<Ticket> findByStatus(Status status);
}
