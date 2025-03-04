package KChat.Aspect;

import KChat.Annotation.ClearRedisCache;
import KChat.Common.Constants;
import KChat.Service.JwtService;
import KChat.Service.RedisCache;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Component
public class ClearRedisCacheAspect {
    private final RedisCache redis;
    private final JwtService jwtService;

    @Autowired
    public ClearRedisCacheAspect(RedisCache redis, JwtService jwtService){
        this.redis = redis;
        this.jwtService = jwtService;
    }

    @Pointcut("@annotation(KChat.Annotation.ClearRedisCache)")
    private void aspect(){}

    @After("aspect()")
    public void clear(JoinPoint joinPoint){
        try {
            Signature signature = joinPoint.getSignature();
            Object[] args = joinPoint.getArgs();
            HttpServletRequest request = (HttpServletRequest) args[args.length-1];
            String userId = jwtService.getUserIdFromToken(request.getHeader(Constants.JwtTokenSign));
            Method method = ((MethodSignature)signature).getMethod();
            ClearRedisCache annotation = method.getAnnotation(ClearRedisCache.class);
            String[] keys = annotation.keys();
            for(String key:keys)
            {
                String toOperate = String.format("Caching_%s_%s",userId,key);
                if(redis.has(toOperate))
                    redis.remove(toOperate);
            }
        }
        catch (Throwable throwable){
            throwable.printStackTrace();
        }
    }
}
