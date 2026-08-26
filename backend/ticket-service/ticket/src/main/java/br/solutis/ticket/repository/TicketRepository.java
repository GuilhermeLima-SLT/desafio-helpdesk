package br.solutis.ticket.repository;

import br.solutis.ticket.entity.Ticket;
import br.solutis.ticket.enums.category.Category;
import br.solutis.ticket.enums.status.Status;
import br.solutis.ticket.enums.priority.TicketPriority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    List<Ticket> findByStatus(Status status);

    List<Ticket> findByPriority(TicketPriority priority);

    List<Ticket> findByCategory(Category category);

    List<Ticket> findByCustomerId(UUID customerId);
}
