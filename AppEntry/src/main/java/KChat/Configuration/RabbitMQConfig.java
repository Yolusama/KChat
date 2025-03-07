package KChat.Configuration;

import KChat.Entity.UserApply;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String QueueName = "UserApply_queue";
    private final String exchangeName = String.format("%s_exchange",UserApply.class.getSimpleName());
    private final String routeKey =  UserApply.class.getSimpleName();

    public String getExchangeName() {
        return exchangeName;
    }

    public String getRouteKey() {
        return routeKey;
    }

    @Bean
    public Queue userApplyQueue(){
        return new Queue(QueueName);
    }

    @Bean
    public FanoutExchange userApplyExchange(){
        return new FanoutExchange(exchangeName);
    }

    @Bean
    public Binding userApplyBinding(Queue queue, FanoutExchange exchange) {
        return new Binding(queue.getName(),Binding.DestinationType.QUEUE,exchange.getName(),
               routeKey,null);
    }
}
