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
    public ResponseEntity<List<TicketResponse>> getByStatus(@Valid @PathVariable Status status) {
        List<TicketResponse> response = ticketRepository.findByStatus(status)
                .stream()
                .map(TicketMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }


    // Implementado com conexão gerenciada por DTO e Mapper para retorno mais adequado às requisições...
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getById(@Valid @PathVariable UUID id) {

        return ticketRepository.findById(id)
                .map(ticket -> ResponseEntity.ok(TicketMapper.toResponse(ticket)))
                .orElse(ResponseEntity.notFound().build());
    }


    //Metodos agora adequados com conexões feitas via DTO e Mapper
    //Usando Stream, DTO
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TicketResponse>> getByPriority(@Valid @PathVariable TicketPriority priority) {
        List<TicketResponse> response = ticketRepository.findByPriority(priority)
                .stream()
                .map(TicketMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/category/{category}")
    public ResponseEntity<List<TicketResponse>> getByCategory(@Valid @PathVariable Category category) {
        List<TicketResponse> response = ticketRepository.findByCategory(category)
                .stream()
                .map(TicketMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }


    // Metodo ainda com conexão direta na Entity
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TicketResponse>> getByCustomerId(@Valid @PathVariable UUID customerId) {
        List<TicketResponse> response = ticketRepository.findByCustomerId(customerId)
                .stream()
                .map(TicketMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
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
    public ResponseEntity<TicketResponse> encerraTicket(@Valid @PathVariable UUID id) {

        return ticketRepository.findById(id)
                .map(ticket -> {

                    ticket.setStatus(Status.valueOf("CLOSED"));

                    Ticket saved = ticketRepository.save(ticket);

//                    rabbitTemplate.convertAndSend("ticket.closed", saved);

                    return ResponseEntity.ok(TicketMapper.toResponse(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@Valid @PathVariable UUID id) {

        if (!ticketRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        ticketRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}