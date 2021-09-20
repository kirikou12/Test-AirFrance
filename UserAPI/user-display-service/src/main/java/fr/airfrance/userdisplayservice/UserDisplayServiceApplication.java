package fr.airfrance.userdisplayservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(scanBasePackages = {"fr.airfrance"})
@EnableMongoRepositories(basePackages = {"fr.airfrance"})
@OpenAPIDefinition(info = @Info(title = "User display service", version = "1.0", description = "This service is a part of the User API. It allows the display of a user."))
public class UserDisplayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserDisplayServiceApplication.class, args);
    }

}
