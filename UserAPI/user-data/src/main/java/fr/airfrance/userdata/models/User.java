package fr.airfrance.userdata.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import fr.airfrance.userdata.validators.DateOfBirth;
import fr.airfrance.userdata.validators.OnCreate;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * User object
 */
@Data
@Setter @Getter @Builder
@NoArgsConstructor @AllArgsConstructor
@Document
public class User implements Serializable {

    @Null(groups = OnCreate.class, message = "{user.id.null}")
    @Id
    private String id;

    @NotBlank(message = "{user.firstName.notBlank}")
    @Field(value = "first_name")
    private String firstName;

    @NotBlank(message = "{user.lastName.notBlank}")
    @Field(value = "last_name")
    private String lastName;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @DateOfBirth(message = "{user.dateOfBirth.invalid}")
    @Field(value = "date_of_birth")
    private LocalDate dateOfBirth;

    @Email(message = "{user.email.invalid}" /*, flags = Pattern.Flag.CASE_INSENSITIVE*/)
    @NotBlank(message = "{user.email.notBlank}")
    @Indexed(unique = true)
    private String email;

    @NotBlank(message = "{user.password.notBlank}")
    @Size(min = 8, max = 14, message = "{user.password.size}")
    private String password;

    private boolean active = true;

    @NotNull(message = "{user.address.notNull}")
   private @Valid Address address;

}