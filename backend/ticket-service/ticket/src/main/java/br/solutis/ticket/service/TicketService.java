package br.solutis.ticket.service;

import br.solutis.ticket.amqp.TicketAMQPConfiguration;
import br.solutis.ticket.dto.event.TicketEvent;
import br.solutis.ticket.dto.request.AtribuirTecnicoRequest;
import br.solutis.ticket.dto.request.TicketRequest;
import br.solutis.ticket.dto.response.TicketResponse;
import br.solutis.ticket.entity.Ticket;
import br.solutis.ticket.enums.Category;
import br.solutis.ticket.enums.Status;
import br.solutis.ticket.enums.TicketPriority;
import br.solutis.ticket.mapper.TicketMapper;
import br.solutis.ticket.repository.TicketRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;
    private RabbitTemplate rabbitTemplate;

    public TicketService(TicketRepository ticketRepository, RabbitTemplate rabbitTemplate) {
        this.ticketRepository = ticketRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public TicketResponse criaTicket(TicketRequest request) {
        Ticket ticket = TicketMapper.toEntity(request);
        ticket.setStatus(Status.OPEN);

        Ticket saved = ticketRepository.save(ticket);

        TicketEvent event = new TicketEvent(
                saved.getId(),
                "TicketCreated",
                saved.getTitle(),
                saved.getStatus().name(),
                saved.getTechnicianId()
        );

        rabbitTemplate.convertAndSend(
                TicketAMQPConfiguration.EXCHANGE,
                TicketAMQPConfiguration.RK_CREATED,
                event
        );

        return TicketMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TicketRequest> findAll() {
        return ticketRepository.findAll()
                .stream()
                .map(TicketRequest::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getByStatus(Status status) {
        return ticketRepository.findByStatus(status)
                .stream()
                .map(TicketMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TicketResponse getById(UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket não encontrado"));
        return TicketMapper.toResponse(ticket);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getByPriority(TicketPriority priority) {
        return ticketRepository.findByPriority(priority)
                .stream()
                .map(TicketMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getByCategory(Category category) {
        return ticketRepository.findByCategory(category)
                .stream()
                .map(TicketMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getByCustomerId(UUID customerId) {
        return ticketRepository.findByCustomerId(customerId)
                .stream()
                .map(TicketMapper::toResponse)
                .toList();
    }

    @Transactional
    public TicketResponse atualizaTicket(UUID id, TicketRequest request) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket não encontrado"));

        ticket.setDescription(request.description());
        ticket.setPriority(request.priority());
        ticket.setCategory(request.category());
        ticket.setStatus(request.status());

        Ticket saved = ticketRepository.save(ticket);

        // rabbitTemplate.convertAndSend("ticket.updated", saved);

        return TicketMapper.toResponse(saved);
    }

    @Transactional
    public TicketResponse atribuirTecnico(UUID id, AtribuirTecnicoRequest request) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket não encontrado"));

        ticket.setTechnicianId(request.technicianId());
        ticket.setStatus(Status.valueOf("IN_PROGRESS"));

        Ticket saved = ticketRepository.save(ticket);

        // rabbitTemplate.convertAndSend("ticket.assigned", saved);

        return TicketMapper.toResponse(saved);
    }

    @Transactional
    public TicketResponse encerraTicket(UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket não encontrado"));

        ticket.setStatus(Status.valueOf("CLOSED"));

        Ticket saved = ticketRepository.save(ticket);

        // rabbitTemplate.convertAndSend("ticket.closed", saved);

        return TicketMapper.toResponse(saved);
    }

    @Transactional
    public void deleteTicket(UUID id) {
        if (!ticketRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket não encontrado");
        }
        ticketRepository.deleteById(id);
    }
}
