package KChat.Service;

import KChat.Configuration.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MQMsgConsumer {
    @RabbitListener(queues = RabbitMQConfig.QueueName)
    public void listen(){

    }
}
