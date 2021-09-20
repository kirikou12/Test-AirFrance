package fr.airfrance.userdata.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Address object
 */
@Data
@Setter @Getter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Address {

    @Id
    private String id;

    @Min(value = 1, message = "{address.houseNumber.minValue}")
    @NotNull(message = "{address.houseNumber.notNull}")
    @Field(value = "house_number")
    private Integer houseNumber;

    @NotBlank(message = "{address.street.notBlank}")
    private String street;

    @NotBlank(message = "{address.cityName.notBlank}")
    @Field(value = "city_name")
    private String cityName;

    @Pattern(regexp = "^(?!0)[0-9]{5}$", message = "{address.zipCode.invalid}")
    @NotNull(message = "{address.zipCode.notNull}")
    @Field(value = "zip_code")
    private String zipCode;


    @Pattern(regexp="^([F-f]rance)$",message="{address.countryName.invalid}")
    @NotBlank(message="{address.countryName.invalid}")
    @Field(value = "country_name")
    private String countryName;

}
