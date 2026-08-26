package br.solutis.ticket.controller;

import br.solutis.ticket.dto.CreateTicketRequest;
import br.solutis.ticket.entity.Ticket;
import br.solutis.ticket.enums.category.Category;
import br.solutis.ticket.enums.priority.TicketPriority;
import br.solutis.ticket.enums.status.Status;
import br.solutis.ticket.repository.TicketRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody @NonNull Ticket ticket) {
        ticket.setStatus(Status.valueOf("OPEN"));
        ticket.setCreatedAt(LocalDateTime.now());
        Ticket saved = ticketRepository.save(ticket);
//        rabbitTemplate.convertAndSend("ticket.created", saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping // Requisição HTTP GET para listar todos os tickets com controle de visibilidade e filtragem (DTO CreateTicketRequest)
    public ResponseEntity<List<CreateTicketRequest>> findAll(){
        List<CreateTicketRequest> list = ticketRepository.findAll().stream().map(CreateTicketRequest::new).toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Ticket>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(ticketRepository.findByStatus(Status.valueOf(status)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getById(@PathVariable @NonNull UUID id) {
        return ticketRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<Ticket>> getByPriority(@PathVariable String priority) {
        return ResponseEntity.ok(ticketRepository.findByPriority(TicketPriority.valueOf(priority)));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Ticket>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(ticketRepository.findByCategory(Category.valueOf(category)));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Ticket>> getByCustomerId(@PathVariable UUID customerId) {
        return ResponseEntity.ok(ticketRepository.findByCustomerId(customerId));
    }
}