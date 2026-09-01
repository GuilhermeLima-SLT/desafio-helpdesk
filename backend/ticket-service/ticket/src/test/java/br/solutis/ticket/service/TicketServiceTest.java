package br.solutis.ticket.service;

import br.solutis.ticket.amqp.TicketAMQPConfiguration;
import br.solutis.ticket.client.UserClient;
import br.solutis.ticket.dto.event.TicketEvent;
import br.solutis.ticket.dto.externo.UserDTO;
import br.solutis.ticket.dto.request.TicketRequest;
import br.solutis.ticket.dto.response.TicketResponse;
import br.solutis.ticket.entity.Ticket;
import br.solutis.ticket.enums.Category;
import br.solutis.ticket.enums.Status;
import br.solutis.ticket.enums.TicketPriority;
import br.solutis.ticket.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import br.solutis.ticket.dto.request.AtribuirTecnicoRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    TicketRepository ticketRepository;
    @Mock
    RabbitTemplate rabbitTemplate;
    @Mock
    UserClient userClient;

    TicketService ticketService;

    @BeforeEach
    void setUp() {
        // Constroi o serviço com os mocks para servir de modelo para os testes necessarios.
        ticketService = new TicketService(ticketRepository, rabbitTemplate, userClient);
    }

    @Test
    void criaTicket_quandoClienteExiste_salvaComStatusOpenEPublicaEvento() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        TicketRequest request = new TicketRequest(
                "PC não liga", "Não dá vídeo", TicketPriority.HIGH, null, Category.HARDWARE, clienteId);

        // o cliente EXISTE no user-service (mock do UserClient)
        when(userClient.buscaPorId(clienteId))
                .thenReturn(Optional.of(new UserDTO(clienteId, "Maria", "CLIENT")));

        // o repositório "salva" e devolve o ticket já com um id
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocacao -> {
            Ticket t = invocacao.getArgument(0);
            t.setId(UUID.randomUUID());
            return t;
        });

        // Act
        TicketResponse resposta = ticketService.criaTicket(request);

        // Assert
        assertThat(resposta.title()).isEqualTo("PC não liga");
        assertThat(resposta.status()).isEqualTo(Status.OPEN);           // Set de status inicial como 'OPEN'
        verify(ticketRepository).save(any(Ticket.class));         // salva ticket
        verify(rabbitTemplate).convertAndSend(                         // publica o evento certo
        eq(TicketAMQPConfiguration.EXCHANGE),                          // exchange ticket.event
                eq(TicketAMQPConfiguration.RK_CREATED),                // 'routing key' ticket.event.created
                any(TicketEvent.class));
    }

    @Test
    void criaTicket_quandoClienteNaoExiste_lancaBadRequestENaoSalvaNemPublica() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        TicketRequest request = new TicketRequest(
                "PC não liga", "Não dá vídeo", TicketPriority.HIGH, null, Category.HARDWARE, clienteId);

        when(userClient.buscaPorId(clienteId)).thenReturn(Optional.empty()); // user-service diz  "não existe"

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> ticketService.criaTicket(request));

        assertThat(erro.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(erro.getReason()).contains("Cliente não encontrado");

        // Mais importante: nada acontece depois de falhar
        verify(ticketRepository, never()).save(any());
        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    void atribuirTecnico_quandoUsuarioNaoEhTecnico_lancaBadRequest() {
        // Arrange
        UUID ticketId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        Ticket existente = new Ticket();
        existente.setId(ticketId);
        existente.setTitle("Impressora travada");
        existente.setStatus(Status.OPEN);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(existente));
        when(userClient.buscaPorId(usuarioId))
                .thenReturn(Optional.of(new UserDTO(usuarioId, "Maria", "CLIENT"))); // existe, mas é CLIENT

        // Act + Assert
        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> ticketService.atribuirTecnico(ticketId, new AtribuirTecnicoRequest(usuarioId)));

        assertThat(erro.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(erro.getReason()).contains("não é tecnico");

        verify(ticketRepository, never()).save(any());
        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    void getById_quandoTicketNaoExiste_lancaNotFound() {
        UUID id = UUID.randomUUID();
        when(ticketRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> ticketService.getById(id));

        assertThat(erro.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
