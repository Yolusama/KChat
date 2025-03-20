package KChat.Interceptor;

import KChat.Common.Constants;
import KChat.Service.JwtService;
import KChat.Service.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtAuthorizer implements HandlerInterceptor
{
    private final RedisCache redis;
    private final JwtService jwtService;
    private final String[] NotInterceptedPatterns;

    @Autowired
    public JwtAuthorizer(RedisCache redis, JwtService jwtService)
    {
        this.redis = redis;
        this.jwtService = jwtService;
        NotInterceptedPatterns = new String[]{
                "/Api/User/Login","/Api/Common/SSE/**",
                "/Api/Common/Heartbeat","/Api/User/Register/**",
                "/Api/User/VerifyToken","/Api/User/GetCheckCode/**","/Api/User/TestRegister"
        };
    }

    public String[] getNotInterceptedPatterns() {
        return NotInterceptedPatterns;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if(!(handler instanceof HandlerMethod))
            return true;
        try {
            String token = request.getHeader(Constants.JwtTokenSign);
            if(token==null)
            {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            }
            String userId = jwtService.getUserIdFromToken(token);
            if(userId==null){
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            }
            if(userId.equals(Constants.TokenExpireSign)){
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            }
            String key = String.format("%s_%s",userId,Constants.JwtTokenSign);
            if(!redis.has(key)){
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            }
            boolean res = redis.get(key).equals(token);
            if(!res)
            {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            }
            return true;
        }
        catch (Exception ex)
        {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

}
