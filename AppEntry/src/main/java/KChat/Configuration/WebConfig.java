package KChat.Configuration;

import KChat.Interceptor.JwtAuthorizer;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private ResourceConfig resourceConfig;

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
        registry.addResourceHandler(resourceConfig.img().getPattern())
                .addResourceLocations(resourceConfig.img().getLocation());

        registry.addResourceHandler(resourceConfig.file().getPattern())
                .addResourceLocations(resourceConfig.file().getLocation());
    }
}
