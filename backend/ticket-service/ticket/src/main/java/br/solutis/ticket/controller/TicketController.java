package br.solutis.ticket.controller;

import br.solutis.ticket.dto.request.AtribuirTecnicoRequest;
import br.solutis.ticket.dto.request.TicketRequest;
import br.solutis.ticket.dto.response.TicketResponse;
import br.solutis.ticket.entity.Ticket;
import br.solutis.ticket.enums.category.Category;
import br.solutis.ticket.enums.priority.TicketPriority;
import br.solutis.ticket.enums.status.Status;
import br.solutis.ticket.mapper.TicketMapper;
import br.solutis.ticket.repository.TicketRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

//    @PostMapping
//    public ResponseEntity<Ticket> createTicket(@RequestBody @NonNull Ticket ticket) {
//        ticket.setStatus(Status.valueOf("OPEN"));
//        ticket.setCreatedAt(LocalDateTime.now());
//        Ticket saved = ticketRepository.save(ticket);
//        rabbitTemplate.convertAndSend("ticket.created", saved);
//        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
//    }

    @PostMapping
    public ResponseEntity<TicketResponse> criaTicket(@Valid @RequestBody TicketRequest request) {

        Ticket ticket = TicketMapper.toEntity(request);
        ticket.setStatus(Status.valueOf("OPEN"));

        Ticket saved = ticketRepository.save(ticket);

//      rabbitTemplate.convertAndSend("ticket.created", saved);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(TicketMapper.toResponse(saved));
}

    @GetMapping // Requisição HTTP GET para listar todos os tickets com controle de visibilidade e filtragem (DTO CreateTicketRequest)
    public ResponseEntity<List<TicketRequest>> findAll(){
        List<TicketRequest> list = ticketRepository.findAll().stream().map(TicketRequest::new).toList();
        return ResponseEntity.ok(list);
    }


    @GetMapping("/status/{status}")
    public ResponseEntity<List<Ticket>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(ticketRepository.findByStatus(Status.valueOf(status)));
    }

// Meu metodo antigo usando conexão direta com entity
//    @GetMapping("/{id}")
//    public ResponseEntity<Ticket> getById(@PathVariable @NonNull UUID id) {
//        return ticketRepository.findById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getById(@PathVariable UUID id) {

        return ticketRepository.findById(id)
                .map(ticket -> ResponseEntity.ok(TicketMapper.toResponse(ticket)))
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

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponse> atualizaTicket(
            @Valid
            @PathVariable UUID id,
            @RequestBody TicketRequest request
    ) {

        return ticketRepository.findById(id)
                .map(ticket -> {
                    ticket.setDescription(request.description());
                    ticket.setPriority(request.priority());
                    ticket.setCategory(request.category());

                    Ticket saved = ticketRepository.save(ticket);

//                    rabbitTemplate.convertAndSend("ticket.updated", saved);

                    return ResponseEntity.ok(TicketMapper.toResponse(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<TicketResponse> atribuirTecnico(
            @Valid
            @PathVariable UUID id,
            @RequestBody AtribuirTecnicoRequest request
    ) {

        return ticketRepository.findById(id)
                .map(ticket -> {

                    ticket.setTechnicianId(request.technicianId());
                    ticket.setStatus(Status.valueOf("IN_PROGRESS"));

                    Ticket saved = ticketRepository.save(ticket);

//                    rabbitTemplate.convertAndSend("ticket.assigned", saved);

                    return ResponseEntity.ok(TicketMapper.toResponse(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/close")
    public ResponseEntity<TicketResponse> encerraTicket(@PathVariable UUID id) {

        return ticketRepository.findById(id)
                .map(ticket -> {

                    ticket.setStatus(Status.valueOf("CLOSED"));

                    Ticket saved = ticketRepository.save(ticket);

//                    rabbitTemplate.convertAndSend("ticket.closed", saved);

                    return ResponseEntity.ok(TicketMapper.toResponse(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Novo endpoint -> Deletar ticket do banco de dados de tickets
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable UUID id) {

        if (!ticketRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        ticketRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}