package KChat.Service;

import KChat.Common.Constants;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SSEService {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void push(String userId,Object payload){
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().data(payload));
            } catch (IOException e) {
                removeEmitter(userId);
            }
        }
    }

    public SseEmitter subscribe(String userId) {
        SseEmitter emitter = new SseEmitter(Constants.SSETimeOut.toMillis());
        emitters.put(userId, emitter);

        emitter.onCompletion(()->removeEmitter(userId));
        emitter.onTimeout(() -> removeEmitter(userId));
        emitter.onError((e) -> removeEmitter(userId));

        return emitter;
    }

    public void removeEmitter(String userId){
        emitters.remove(userId);
    }
}
