package FileServer.GetFile.Configs;

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

    // Name of the durable queue, initialized in the constructor
    public String DURABLE_QUEUE;

    /**
     * Constructor to initialize the durable queue name with the container name.
     * The durable queue is unique per container, using the container name as part of the queue name.
     */
    public RabbitMQConfig() {
        this.DURABLE_QUEUE = "durable_queue_" + getContainerName();
    }

    /**
     * Defines the Fanout Exchange bean.
     * A Fanout Exchange routes messages to all bound queues, irrespective of any routing key.
     *
     * @return A FanoutExchange object configured with the exchange name.
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    /**
     * Defines the durable queue bean.
     * A durable queue is one that persists across broker restarts, ensuring message durability.
     *
     * @return A Queue object representing the durable queue.
     */
    @Bean
    public Queue durableQueue() {
        return new Queue(DURABLE_QUEUE, true); // true indicates the queue is durable
    }

    /**
     * Binds the durable queue to the Fanout Exchange.
     * This ensures that any message sent to the Fanout Exchange is broadcast to the bound queue.
     *
     * @param fanoutExchange The Fanout Exchange bean.
     * @param durableQueue   The durable Queue bean.
     * @return A Binding object representing the connection between the exchange and the queue.
     */
    @Bean
    public Binding binding(FanoutExchange fanoutExchange, Queue durableQueue) {
        return BindingBuilder.bind(durableQueue).to(fanoutExchange);
    }

    /**
     * Provides a Jackson2JsonMessageConverter to convert messages to and from JSON format.
     * This allows messages to be exchanged in a structured, readable format.
     *
     * @return A Jackson2JsonMessageConverter for message serialization and deserialization.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Reads the container name from the `/etc/hostname` file.
     * Docker containers typically use the hostname to reflect the container name.
     * If there's an error reading the file, a default name is returned.
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
        System.out.println("Container Name: " + containerName);
        return containerName;
    }

    /**
     * Constructs the queue name using the durable queue name and container name.
     * This ensures that each container gets its own unique queue.
     *
     * @return The full queue name as a String.
     */
    public String getQueueName() {
        String containerName = getContainerName();
        return DURABLE_QUEUE + containerName;
    }
}
