package com.scrumandcoke.movietheaterclub.configuration;

import com.scrumandcoke.movietheaterclub.interceptor.LoginRequiredInterceptor;
import com.scrumandcoke.movietheaterclub.interceptor.UserTypesAllowedInterceptor;
import com.scrumandcoke.movietheaterclub.service.IAMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private IAMService iamService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginRequiredInterceptor(iamService));
        registry.addInterceptor(new UserTypesAllowedInterceptor());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "https://bee5-2601-646-a100-cbf0-e164-ed22-95cd-68aa.ngrok-free.app")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

