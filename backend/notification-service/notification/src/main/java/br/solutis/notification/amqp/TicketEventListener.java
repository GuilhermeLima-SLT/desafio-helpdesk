package br.solutis.notification.amqp;

import br.solutis.notification.dto.event.TicketEvent;
import br.solutis.notification.entity.Notification;
import br.solutis.notification.repository.NotificationRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TicketEventListener {
    private final NotificationRepository repository;

    public TicketEventListener(NotificationRepository repository){
        this.repository = repository;
    }

    @RabbitListener(queues = NotificationAMQPConfiguration.QUEUE)
    public void onTicketEvent(TicketEvent event){
        Notification n = new Notification();
        n.setTicketId(event.ticketId());
        n.setType(event.eventType());
        n.setMessage(montarMensagem(event));

        repository.save(n);
        System.out.println("Notificação registrada: " + event.eventType() + " -> ticket " + event.ticketId());
    }

    private String montarMensagem(TicketEvent e){
        return switch (e.eventType()){
            case "TicketCreated" -> "Chamado '" + e.title() + "' criado (status " + e.status() + ").";
            case "TicketAssigned" -> "Chamado '" + e.title() + "' atribuido ao tecnico " + e.technicianId() + ".";
            case "TicketStatusChanged" -> "Chamado '" + e.title() + "' mudou para status " + e.status() + ").";
            default -> "Evento " + e.eventType() + " no chamado '" + e.title() + "'.";
        };
    };
}
