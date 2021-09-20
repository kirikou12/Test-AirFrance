package fr.airfrance.userdata.validation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Object that contains all validation messages for a User
 */
@Component
@ConfigurationProperties(prefix = "user")
@PropertySource(value = "classpath:ValidationMessages.properties", encoding = "ISO-8859-1")
@Getter @Setter
public class UserValidationMessages {

    Map<String, String> firstName;
    Map<String, String> lastName;
    Map<String, String> dateOfBirth;
    Map<String, String> email;
    Map<String, String> password;
    Map<String, String> address;

}
