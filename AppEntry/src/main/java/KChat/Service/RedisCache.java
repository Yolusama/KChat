package KChat.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisCache {
    private final RedisTemplate<String,Object> template;

    @Autowired
    public RedisCache(RedisTemplate<String,Object> template)
    {
        this.template = template;
    }

    public void set(String key, Object value, Duration duration)
    {
        template.opsForValue().set(key,value,duration);
    }

    public void set(String key,Object value)
    {
        template.opsForValue().set(key,value);
    }

    public Object get(String key)
    {
        return template.opsForValue().get(key);
    }

    public Boolean has(String key)
    {
        return template.hasKey(key);
    }

    public Long getExpire(String key){return template.getExpire(key);}

    public void remove(String key)
    {
        template.delete(key);
    }
}
