package KChat.Configuration;

import KChat.Interceptor.JwtAuthorizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {
    @Resource
    private JwtAuthorizer jwtAuthorizer;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/Api/**").allowedMethods("*")
                .allowedOrigins("*")
                .allowedHeaders("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
       registry.addInterceptor(jwtAuthorizer).
               excludePathPatterns(jwtAuthorizer.getNotInterceptedPatterns());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    }
}
