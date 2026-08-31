package br.solutis.ticket.service;

import br.solutis.ticket.amqp.TicketAMQPConfiguration;
import br.solutis.ticket.client.UserClient;
import br.solutis.ticket.dto.event.TicketEvent;
import br.solutis.ticket.dto.externo.UserDTO;
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
    private final UserClient userClient;

    public TicketService(TicketRepository ticketRepository, RabbitTemplate rabbitTemplate, UserClient userClient) {
        this.ticketRepository = ticketRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.userClient = userClient;
    }

    // Metodo para substituir bloco anterior de montagem de eventos e enviar para message broker (RabbitMQ)
    private void publicarEvento(Ticket ticket, String eventType, String routingKey){
        TicketEvent event = new TicketEvent(
                ticket.getId(),
                eventType,
                ticket.getTitle(),
                ticket.getStatus().name(),
                ticket.getTechnicianId()
        );
        rabbitTemplate.convertAndSend(TicketAMQPConfiguration.EXCHANGE, routingKey, event);
    }

    @Transactional
    public TicketResponse criaTicket(TicketRequest request) {

        // Validacao de ser solicitante como cliente registrado em banco de dados EXTERNO do serviço de usuarios usando algortimo explicito do 'loadBalanced'
        // anotacao do "RestClient" construido com DTO por conflito de uso do loadBalanced pelo servico do Eureka (Discovery)
        UserDTO cliente = userClient.buscaPorId(request.customerId()).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "ID de Cliente não encontrado: " + request.customerId())
        );

        Ticket ticket = TicketMapper.toEntity(request);
        ticket.setStatus(Status.OPEN);

        Ticket saved = ticketRepository.save(ticket);

        publicarEvento(saved, "TicketCreated", TicketAMQPConfiguration.RK_CREATED);

        return TicketMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> findAll() {
        return ticketRepository.findAll()
                .stream()
                .map(TicketMapper::toResponse)
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

        Status statusAntigo = ticket.getStatus(); //captaçao antes (auxiliar)

        ticket.setDescription(request.description());
        ticket.setPriority(request.priority());
        ticket.setCategory(request.category());
        ticket.setStatus(request.status());

        Ticket saved = ticketRepository.save(ticket);

        if (saved.getStatus() != statusAntigo){ // Se status de antes da atualizacao diferente de pos-atualizacao...
            publicarEvento(saved, "TicketStatusChanged", TicketAMQPConfiguration.RK_STATUS_CHANGED);
        }
        return TicketMapper.toResponse(saved);
    }

    @Transactional
    public TicketResponse atribuirTecnico(UUID id, AtribuirTecnicoRequest request) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket não encontrado"));

        // Validacao de ser tecnico registrado em banco de dados EXTERNO do serviço de usuarios usando algortimo explicito do 'loadBalanced'
        // anotacao do "RestClient" construido com DTO por conflito de uso do loadBalanced pelo servico do Eureka (Discovery)
        UserDTO tecnico = userClient.buscaPorId(request.technicianId()).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "ID do Tecnico: " + request.technicianId() + " não encontrado"));

        if (!"TECHNICIAN".equals(tecnico.role())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O usuario: "+ request.technicianId() + " informado não é tecnico");
        }

        ticket.setTechnicianId(request.technicianId());
        ticket.setStatus(Status.IN_PROGRESS); // Correcao, usando ENUM direto

        Ticket saved = ticketRepository.save(ticket);

        publicarEvento(saved, "TicketAssigned", TicketAMQPConfiguration.RK_ASSIGNED);

        return TicketMapper.toResponse(saved);
    }

    @Transactional
    public TicketResponse encerraTicket(UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket não encontrado"));

        ticket.setStatus(Status.CLOSED); // Correcao, usando ENUM direto

        Ticket saved = ticketRepository.save(ticket);

        publicarEvento(saved, "TicketStatusChanged", TicketAMQPConfiguration.RK_STATUS_CHANGED);

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
