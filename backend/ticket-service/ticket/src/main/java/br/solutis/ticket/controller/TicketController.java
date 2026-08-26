package br.solutis.ticket.controller;

import br.solutis.ticket.dto.request.AtribuirTecnicoRequest;
import br.solutis.ticket.dto.request.TicketRequest;
import br.solutis.ticket.dto.response.TicketResponse;
import br.solutis.ticket.entity.Ticket;
import br.solutis.ticket.enums.Category;
import br.solutis.ticket.enums.TicketPriority;
import br.solutis.ticket.enums.Status;
import br.solutis.ticket.mapper.TicketMapper;
import br.solutis.ticket.repository.TicketRepository;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Criar ticket com título, descrição, categoria e prioridade")
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

    @Operation(summary = "Listar todos tickets")
    @GetMapping // Requisição HTTP GET para listar todos os tickets com controle de visibilidade e filtragem (DTO CreateTicketRequest)
    public ResponseEntity<List<TicketRequest>> findAll(){
        List<TicketRequest> list = ticketRepository.findAll().stream().map(TicketRequest::new).toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Filtro de tickets por status especifico")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TicketResponse>> getByStatus(@Valid @PathVariable Status status) {
        List<TicketResponse> response = ticketRepository.findByStatus(status)
                .stream()
                .map(TicketMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar ticket especifico pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getById(@Valid @PathVariable UUID id) {

        return ticketRepository.findById(id)
                .map(ticket -> ResponseEntity.ok(TicketMapper.toResponse(ticket)))
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(summary = "Filtro de tickets por prioridade especifica")
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TicketResponse>> getByPriority(@Valid @PathVariable TicketPriority priority) {
        List<TicketResponse> response = ticketRepository.findByPriority(priority)
                .stream()
                .map(TicketMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Filtro de tickets por categoria especifica")
    @GetMapping("/category/{category}")
    public ResponseEntity<List<TicketResponse>> getByCategory(@Valid @PathVariable Category category) {
        List<TicketResponse> response = ticketRepository.findByCategory(category)
                .stream()
                .map(TicketMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Consultar chamados de determinado cliente")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TicketResponse>> getByCustomerId(@Valid @PathVariable UUID customerId) {
        List<TicketResponse> response = ticketRepository.findByCustomerId(customerId)
                .stream()
                .map(TicketMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Alterar prioridade, categoria, descrição e status de um ticket pelo ID")
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
                    ticket.setStatus(request.status());

                    Ticket saved = ticketRepository.save(ticket);

//                    rabbitTemplate.convertAndSend("ticket.updated", saved);

                    return ResponseEntity.ok(TicketMapper.toResponse(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Atribuir técnico")
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


    @Operation(summary = "Encerrar ticket")
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

    @Operation(summary = "Deletar ticket")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@Valid @PathVariable UUID id) {

        if (!ticketRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        ticketRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}