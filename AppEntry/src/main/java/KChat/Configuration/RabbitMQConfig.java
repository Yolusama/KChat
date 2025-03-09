package KChat.Configuration;

import KChat.Entity.ChatMessage;
import KChat.Entity.UserApply;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class RabbitMQConfig {
    public static final String QueueName1 = "UserApplyQueue";
    public static final String QueueName2 = "MessageNotifyQueue";
    private final String exchangeName = "Exchange";
    private final String routeKey1 =  UserApply.class.getSimpleName();
    private final String routeKey2 = ChatMessage.class.getSimpleName();

    public String getExchangeName() {
        return exchangeName;
    }

    public String getApplyKey() {
        return routeKey1;
    }
    public String getMessageKey() {
        return routeKey2;
    }

    @Bean(name = "UserApply")
    public Queue userApplyQueue(){
        return new Queue(QueueName1);
    }

    @Bean(name = "MessageNotify")
     Queue messageQueue(){
        return new Queue(QueueName2);
    }

    @Bean
    public FanoutExchange exchange(){
        return new FanoutExchange(exchangeName);
    }

    @Bean
    public Binding userApplyBinding(@Autowired @Qualifier("UserApply") Queue queue,
                                    FanoutExchange exchange) {
        return new Binding(queue.getName(),Binding.DestinationType.QUEUE,exchange.getName(),
               routeKey1,null);
    }

    @Bean
    public Binding messageBinding(@Autowired @Qualifier("MessageNotify") Queue queue,FanoutExchange exchange){
       return new Binding(queue.getName(),Binding.DestinationType.QUEUE,exchange.getName(),routeKey2,null);
    }
}
