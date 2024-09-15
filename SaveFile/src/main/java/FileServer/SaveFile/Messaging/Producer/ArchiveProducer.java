package FileServer.SaveFile.Messaging.Producer;

import FileServer.SaveFile.Configs.RabbitMQConfig;
import FileServer.SaveFile.Model.Archive;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@EnableRabbit
public class ArchiveProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Constructor that injects the {@link RabbitTemplate} for sending messages to RabbitMQ.
     * The message converter is set to {@link Jackson2JsonMessageConverter} to handle JSON serialization.
     *
     * @param rabbitTemplate the RabbitTemplate used to communicate with RabbitMQ
     */
    @Autowired
    public ArchiveProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        // Set the message converter to use JSON for message serialization
        this.rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
    }

    /**
     * Sends an {@link Archive} object to the RabbitMQ Fanout Exchange.
     * The message is broadcasted to all queues bound to the exchange.
     *
     * @param archive the Archive object to send
     */
    public void send(Archive archive) {
        // Send the archive object to the Fanout Exchange
        rabbitTemplate.convertAndSend(RabbitMQConfig.FANOUT_EXCHANGE, "", archive);
    }
}
