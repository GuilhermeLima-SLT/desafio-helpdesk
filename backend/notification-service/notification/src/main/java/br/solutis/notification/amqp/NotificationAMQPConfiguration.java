package br.solutis.notification.amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class NotificationAMQPConfiguration {

    public static final String EXCHANGE = "ticket.events";
    public static final String QUEUE = "notification.tickets";
    public static final String ROUTING_PATTERN = "ticket.#";

    @Bean
    public TopicExchange ticketEventsExchange(){
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue notificationQueue(){
        return new Queue(QUEUE, true);
    }

    @Bean
    public Binding binding(Queue notificationQueue, TopicExchange ticketEventsExchange){
        return BindingBuilder.bind(notificationQueue).to(ticketEventsExchange).with(ROUTING_PATTERN);
    }

    @Bean
    public MessageConverter jsonMessageConverter(JsonMapper jsonMapper){
        return new JacksonJsonMessageConverter(jsonMapper);
    }
}
