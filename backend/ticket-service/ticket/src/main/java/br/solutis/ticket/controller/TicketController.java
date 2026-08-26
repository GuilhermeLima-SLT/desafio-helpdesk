package br.solutis.ticket.controller;

import br.solutis.ticket.dto.CreateTicketRequest;
import br.solutis.ticket.entity.Ticket;
import br.solutis.ticket.enums.category.Category;
import br.solutis.ticket.enums.priority.TicketPriority;
import br.solutis.ticket.enums.status.Status;
import br.solutis.ticket.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @PostMapping // Requisição HTTP POST para criar um novo ticket
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        return ResponseEntity.ok(ticketRepository.save(ticket));
    }

//    @GetMapping // Requisição HTTP GET (descontinuada) para listar todos os tickets 
    //       (wrapper HTTP > permitindo controlar status de response: 200, 201, 404...)
//    public ResponseEntity<List<Ticket>> listTickets() {
//        return ResponseEntity.ok(ticketRepository.findAll());
//    }
//      Trabalhando diretamente com a entidade (retorna todos os valores da entity sem filtro ou controle de visibilidade) - não é uma boa prática, pois expõe dados sensíveis e internos da aplicação.

    @GetMapping // Requisição HTTP GET para listar todos os tickets com controle de visibilidade e filtragem (DTO CreateTicketRequest)
    public ResponseEntity<List<CreateTicketRequest>> findAll(){
        List<CreateTicketRequest> list = ticketRepository.findAll().stream().map(CreateTicketRequest::new).toList();
        return ResponseEntity.ok(list);
    };

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Ticket>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(ticketRepository.findByStatus(Status.valueOf(status)));
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