package FileServer.SaveFile.Configs;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class RabbitMQConfig {

    // Constant that defines the name of the Fanout Exchange
    public static final String FANOUT_EXCHANGE = "file_fanout_exchange";

    // Name of the durable queue
    public String DURABLE_QUEUE;

    public RabbitMQConfig() {
        this.DURABLE_QUEUE = "durable_queue_" + getContainerName();
    }

    /**
     * This method defines the Fanout Exchange bean.
     * A Fanout Exchange routes messages to all queues bound to it, without considering any routing key.
     *
     * @return FanoutExchange object configured with the exchange name.
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    /**
     * This method defines the durable queue bean.
     * A durable queue ensures that the queue will survive a broker restart.
     *
     * @return Queue object representing a durable queue.
     */
    @Bean
    public Queue durableQueue() {

        return new Queue(DURABLE_QUEUE, true); // true indicates the queue is durable
    }

    /**
     * This method binds the durable queue to the Fanout Exchange.
     * Any message sent to the Fanout Exchange will be delivered to the bound queue.
     *
     * @param fanoutExchange The Fanout Exchange bean.
     * @param durableQueue   The Queue bean to be bound.
     * @return Binding object representing the relationship between the exchange and the queue.
     */
    @Bean
    public Binding binding(FanoutExchange fanoutExchange, Queue durableQueue) {
        return BindingBuilder.bind(durableQueue).to(fanoutExchange);
    }
    /**
     * This method provides a Jackson2JsonMessageConverter that will be used to convert
     * messages to and from JSON format.
     *
     * @return Jackson2JsonMessageConverter for RabbitMQ message conversion
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    /**
     * This method reads the container name from the /etc/hostname file.
     * The hostname inside Docker containers usually corresponds to the container name.
     *
     * @return The container name as a string.
     */
    private String getContainerName() {
        String containerName = "defaultContainer"; // Default value in case of error
        try {
            containerName = new String(Files.readAllBytes(Paths.get("/etc/hostname"))).trim();
        } catch (IOException e) {
            e.printStackTrace(); // Log the error if there's an issue reading the file
        }
        return containerName;
    }
}

