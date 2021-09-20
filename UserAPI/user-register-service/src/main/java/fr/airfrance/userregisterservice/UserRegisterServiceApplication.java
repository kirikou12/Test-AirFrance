package fr.airfrance.userregisterservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@SpringBootApplication(scanBasePackages = "fr.airfrance")
@EnableMongoRepositories(basePackages = {"fr.airfrance"})
public class UserRegisterServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserRegisterServiceApplication.class, args);
    }


    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate(RestTemplateBuilder builder){
        return builder
                //.rootUri("http://localhost:8082/user-api/v1.0/display")
                .build();
    }
}
