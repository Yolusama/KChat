package KChat.Service;

import KChat.Configuration.RabbitMQConfig;
import KChat.Entity.ChatMessage;
import KChat.Entity.UserApply;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MQMsgConsumer {
    @Autowired
    private SSEService sseService;

    @RabbitListener(queues = {RabbitMQConfig.QueueName1,RabbitMQConfig.QueueName2})
    public void listen(String queueAndContactId){
        String[] strs = queueAndContactId.split(" ");
        String queue = strs[0];
        String contactId = strs[1];

        if(queue.equals(RabbitMQConfig.QueueName1))
            listenQueue1(contactId);
        else if(queue.equals(RabbitMQConfig.QueueName2))
            listenQueue2(contactId);
    }

    private void listenQueue1(String contactId){
       Map<String,Object> payload = new HashMap<>();
       payload.put("key","apply");
       payload.put("value",true);
       sseService.push(contactId,payload);
    }

    private void listenQueue2(String contactId){
        Map<String,Object> payload = new HashMap<>();
        payload.put("key","message");
        payload.put("value",true);
        sseService.push(contactId,payload);
    }
}
