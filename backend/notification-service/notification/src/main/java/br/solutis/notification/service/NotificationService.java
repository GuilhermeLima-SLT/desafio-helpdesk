package br.solutis.notification.service;

import br.solutis.notification.dto.response.NotificationResponse;
import br.solutis.notification.entity.Notification;
import br.solutis.notification.repository.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {
    private final NotificationRepository repository;


    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public List<NotificationResponse> listar(){
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public NotificationResponse buscaPorId(UUID id){
        Notification n = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Notificacao não encontrada"));
        return toResponse(n);
    }

    private NotificationResponse toResponse(Notification n){
        return new NotificationResponse(n.getId(),n.getTicketId(), n.getType(), n.getMessage(), n.getCreatedAt());
    }
}
