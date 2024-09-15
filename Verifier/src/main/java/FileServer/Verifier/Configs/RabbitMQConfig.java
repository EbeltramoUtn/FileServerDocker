package FileServer.Verifier.Configs;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    // Constant that defines the name of the Fanout Exchange
    public static final String FANOUT_EXCHANGE = "file_fanout_exchange";
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
}
