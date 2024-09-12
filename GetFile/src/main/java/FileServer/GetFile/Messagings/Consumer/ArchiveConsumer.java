package FileServer.GetFile.Messagings.Consumer;

import FileServer.GetFile.Configs.RabbitMQConfig;
import FileServer.GetFile.Models.Archive;
import FileServer.GetFile.Services.ArchiveService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class ArchiveConsumer {

    private final ArchiveService archiveService;
    private final RabbitMQConfig rabbitMQConfig;

    /**
     * Constructor that injects the {@link ArchiveService} to handle archive-related logic,
     * and the {@link RabbitMQConfig} to configure RabbitMQ settings.
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
     * This method is a RabbitMQ listener that processes messages from the configured durable queue.
     * It uses manual acknowledgment mode, allowing the consumer to manually acknowledge or reject a message.
     *
     * @param archive      the {@link Archive} object received from the queue
     * @param deliveryTag  the delivery tag of the RabbitMQ message, used for acknowledging or rejecting the message
     * @param channel      the RabbitMQ channel, used to perform manual acknowledgment or rejection of messages
     * @throws Exception if there is an issue processing the message or interacting with the RabbitMQ channel
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
                // Acknowledge the message as successfully processed
                channel.basicAck(deliveryTag, false);
            } else {
                // Reject and requeue the message if processing failed
                channel.basicNack(deliveryTag, false, true);
            }
        } catch (Exception e) {
            // Handle any exceptions and requeue the message
            channel.basicNack(deliveryTag, false, true);
            throw e; // Optionally rethrow the exception for further handling
        }
    }
}
