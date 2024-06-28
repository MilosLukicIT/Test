package com.service.user.configuration;

import org.example.EmailNotificationClient;
import org.example.EmailNotificationClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class EmailServiceClientConfig {

    @Bean
    public EmailNotificationClient emailNotificationClient(){
        EmailNotificationClientBuilder builder = new EmailNotificationClientBuilder();
        builder.baseUrl("http://localhost:8081/notifications");
        builder.restClient(RestClient.builder().build());
        return builder.build();
    }
}
