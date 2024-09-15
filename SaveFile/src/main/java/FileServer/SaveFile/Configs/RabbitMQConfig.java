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

    // Constructor to initialize the durable queue name with the container name
    public RabbitMQConfig() {
        this.DURABLE_QUEUE = "durable_queue_" + getContainerName();
    }

    /**
     * Defines a bean for the Fanout Exchange.
     * A Fanout Exchange broadcasts messages to all bound queues, regardless of any routing key.
     *
     * @return A FanoutExchange object configured with a specific exchange name.
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    /**
     * Defines a bean for a durable queue.
     * A durable queue will persist even after the broker restarts, ensuring message delivery.
     *
     * @return A Queue object that represents a durable queue.
     */
    @Bean
    public Queue durableQueue() {
        return new Queue(DURABLE_QUEUE, true); // true indicates the queue is durable
    }

    /**
     * Creates a binding between the Fanout Exchange and the durable queue.
     * This ensures that any message sent to the Fanout Exchange is routed to the durable queue.
     *
     * @param fanoutExchange The Fanout Exchange bean.
     * @param durableQueue   The durable Queue bean.
     * @return A Binding object representing the link between the exchange and the queue.
     */
    @Bean
    public Binding binding(FanoutExchange fanoutExchange, Queue durableQueue) {
        return BindingBuilder.bind(durableQueue).to(fanoutExchange);
    }

    /**
     * Provides a JSON message converter to transform messages to and from JSON format.
     * This is essential for exchanging structured data between services.
     *
     * @return A Jackson2JsonMessageConverter used for message serialization and deserialization.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Retrieves the container name by reading the /etc/hostname file.
     * In Docker environments, the hostname usually corresponds to the container name.
     * If an error occurs during file reading, a default name is returned.
     *
     * @return The container name as a String.
     */
    private String getContainerName() {
        String containerName = "defaultContainer"; // Default value in case of an error
        try {
            containerName = new String(Files.readAllBytes(Paths.get("/etc/hostname"))).trim();
        } catch (IOException e) {
            e.printStackTrace(); // Log the error if reading the file fails
        }
        return containerName;
    }
}
