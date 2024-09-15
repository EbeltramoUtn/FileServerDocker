package FileServer.SaveFile.Messaging.Consumer;

import FileServer.SaveFile.Configs.RabbitMQConfig;
import FileServer.SaveFile.Model.Archive;
import FileServer.SaveFile.Services.ArchiveService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.amqp.support.AmqpHeaders;

@Component
public class ArchiveConsumer {

    private final ArchiveService archiveService;
    private final RabbitMQConfig rabbitMQConfig;

    /**
     * Constructor that injects the {@link ArchiveService} and {@link RabbitMQConfig}
     * to handle archive-related logic and configuration.
     *
     * @param archiveService the service responsible for saving the archive to the database
     * @param rabbitMQConfig the configuration for RabbitMQ, including the queue name
     */
    @Autowired
    public ArchiveConsumer(ArchiveService archiveService, RabbitMQConfig rabbitMQConfig) {
        this.archiveService = archiveService;
        this.rabbitMQConfig = rabbitMQConfig;
    }

    /**
     * RabbitMQ listener that processes messages from the configured durable queue.
     * This method uses manual acknowledgment, meaning the consumer will manually confirm
     * if the message was processed successfully or not.
     *
     * @param archive     the {@link Archive} object received from the queue
     * @param deliveryTag the delivery tag of the message, used to acknowledge or reject it
     * @param channel     the RabbitMQ channel, used for manual acknowledgment or rejection
     * @throws Exception if there is an issue processing the message or interacting with RabbitMQ
     */
    @RabbitListener(queues = "#{rabbitMQConfig.DURABLE_QUEUE}", ackMode = "MANUAL")
    public void receive(
            @Payload Archive archive,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
            @Header(AmqpHeaders.CHANNEL) Channel channel) throws Exception {

        // Attempt to save the archive in the database
        Archive result = archiveService.saveFile(archive);

        try {
            if (result != null) {
                // If the archive is successfully saved, acknowledge the message
                channel.basicAck(deliveryTag, false);
            } else {
                // If saving fails, reject the message and requeue it
                channel.basicNack(deliveryTag, false, true);
            }
        } catch (Exception e) {
            // If an exception occurs, reject the message and requeue it
            channel.basicNack(deliveryTag, false, true);
            throw e; // Rethrow the exception for further handling
        }
    }
}
