package br.solutis.ticket.amqp;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class TicketAMQPConfiguration {

    public static final String EXCHANGE = "ticket.events";
    public static final String RK_CREATED = "ticket.created";
    public static final String RK_ASSIGNED = "ticket.assigned";
    public static final String RK_STATUS_CHANGED = "ticket.status-changed";

    @Bean
    public TopicExchange ticketEventExchange(){
        return new TopicExchange(EXCHANGE, true, false);
    }

    // Conversor JSON
    @Bean
    public MessageConverter jsonMessageConverter(JsonMapper jsonMapper){
        return new JacksonJsonMessageConverter(jsonMapper);
    }
}
