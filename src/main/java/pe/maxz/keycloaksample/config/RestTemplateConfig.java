package pe.maxz.keycloaksample.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    @Bean
    RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
