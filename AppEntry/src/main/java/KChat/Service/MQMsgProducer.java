package KChat.Service;

import KChat.Configuration.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MQMsgProducer {
    @Resource
    private RabbitTemplate template;
    @Autowired
    private RabbitMQConfig config;

    // 发送消息
    public void sendMessage(String message) {
        template.convertAndSend(config.getExchangeName(),config.getRouteKey(),message);
    }
}
