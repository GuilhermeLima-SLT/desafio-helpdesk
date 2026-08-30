package br.solutis.ticket.client;

import br.solutis.ticket.dto.externo.UserDTO;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserClient {

    private static final String USER_SERVICE = "user-service";

    private final DiscoveryClient discoveryClient;
    private final RestClient restClient;

    public UserClient(DiscoveryClient discoveryClient, RestClient.Builder builder){
        this.discoveryClient = discoveryClient;
        this.restClient = builder.build();
    }

    public Optional<UserDTO> buscaPorId(UUID id){
        String baseUrl = resolverBaseUrl();
        try{
            UserDTO user = restClient.get()
                    .uri(baseUrl + "/api/users/{id}", id)
                    .retrieve()
                    .body(UserDTO.class);
            return Optional.ofNullable(user);
        } catch (HttpClientErrorException.NotFound e){
            return Optional.empty();
        }
    }

    private String resolverBaseUrl(){
        List<ServiceInstance> instancias = discoveryClient.getInstances(USER_SERVICE);
        if (instancias.isEmpty()){
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
            "Servico de usuarios indisponivel");
        }
        return instancias.get(0).getUri().toString();
    }
}
