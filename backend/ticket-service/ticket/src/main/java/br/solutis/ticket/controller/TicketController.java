package br.solutis.ticket.controller;

import br.solutis.ticket.dto.request.AtribuirTecnicoRequest;
import br.solutis.ticket.dto.request.TicketRequest;
import br.solutis.ticket.dto.response.TicketResponse;
import br.solutis.ticket.enums.Category;
import br.solutis.ticket.enums.TicketPriority;
import br.solutis.ticket.enums.Status;
import br.solutis.ticket.service.TicketService;
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
    private TicketService ticketService;

    @Operation(summary = "Criar ticket com título, descrição, categoria e prioridade")
    @PostMapping
    public ResponseEntity<TicketResponse> criaTicket(@Valid @RequestBody TicketRequest request) {
        TicketResponse response = ticketService.criaTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar todos tickets")
    @GetMapping
    public ResponseEntity<List<TicketRequest>> findAll() {
        List<TicketRequest> list = ticketService.findAll();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Filtro de tickets por status especifico")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TicketResponse>> getByStatus(@Valid @PathVariable Status status) {
        List<TicketResponse> response = ticketService.getByStatus(status);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar ticket especifico pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getById(@Valid @PathVariable UUID id) {
        TicketResponse response = ticketService.getById(id);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Filtro de tickets por prioridade especifica")
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TicketResponse>> getByPriority(@Valid @PathVariable TicketPriority priority) {
        List<TicketResponse> response = ticketService.getByPriority(priority);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Filtro de tickets por categoria especifica")
    @GetMapping("/category/{category}")
    public ResponseEntity<List<TicketResponse>> getByCategory(@Valid @PathVariable Category category) {
        List<TicketResponse> response = ticketService.getByCategory(category);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Consultar chamados de determinado cliente")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TicketResponse>> getByCustomerId(@Valid @PathVariable UUID customerId) {
        List<TicketResponse> response = ticketService.getByCustomerId(customerId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Alterar prioridade, categoria, descrição e status de um ticket pelo ID")
    @PutMapping("/{id}")
    public ResponseEntity<TicketResponse> atualizaTicket(@Valid @PathVariable UUID id,@RequestBody TicketRequest request){
        TicketResponse response = ticketService.atualizaTicket(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atribuir técnico")
    @PutMapping("/{id}/assign")
    public ResponseEntity<TicketResponse> atribuirTecnico(
            @Valid @PathVariable UUID id,
            @RequestBody AtribuirTecnicoRequest request
    ) {
        TicketResponse response = ticketService.atribuirTecnico(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Encerrar ticket")
    @DeleteMapping("/{id}/close")
    public ResponseEntity<TicketResponse> encerraTicket(@Valid @PathVariable UUID id) {
        TicketResponse response = ticketService.encerraTicket(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Deletar ticket")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@Valid @PathVariable UUID id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

}