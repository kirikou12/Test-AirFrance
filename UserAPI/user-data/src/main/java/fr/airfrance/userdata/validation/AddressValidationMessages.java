package fr.airfrance.userdata.validation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Object that contains all validation messages for an Address.
 */
@Component
@ConfigurationProperties(prefix = "address")
@PropertySource(value = "classpath:ValidationMessages.properties", encoding = "ISO-8859-1")
@Getter @Setter
public class AddressValidationMessages {

    Map<String, String> houseNumber;
    Map<String, String> street;
    Map<String, String> cityName;
    Map<String, String> zipCode;
    Map<String, String> countryName;

}
