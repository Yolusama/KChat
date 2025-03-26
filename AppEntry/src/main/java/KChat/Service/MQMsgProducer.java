package KChat.Service;

import KChat.Configuration.RabbitMQConfig;
import KChat.Entity.ChatMessage;
import KChat.Entity.MessageRecord;
import KChat.Entity.UserApply;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public void produceAndSend(Object obj) {
        Class<?> type = obj.getClass();
        if(type.equals(UserApply.class))
        {
            UserApply apply = (UserApply)obj;
            template.convertAndSend(config.getExchangeName(),config.getApplyKey(),
                    String.format("%s %s",RabbitMQConfig.QueueName1,apply.getContactId()));
        }

        else if(type.equals(MessageRecord.class)){
           MessageRecord record = (MessageRecord) obj;
            template.convertAndSend(config.getExchangeName(),config.getMessageKey(),
                    String.format("%s %s",RabbitMQConfig.QueueName2,record.getContactId()));
        }

    }
}
