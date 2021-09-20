package fr.airfrance.userdata.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Object that represents all the api handled errors.
 */
@Getter
public class UserApiError {

    /**
     * Status of the occurred error.
     */
    private HttpStatus status;

    /**
     * Indicates the time when the error occured.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy hh:mm:ss")
    private LocalDateTime timestamp;

    /**
     * Message of the error.
     */
    private String message;

    /**
     * For debug purposes. Contains the stacktrace of the occurred error.
     */
    private @Setter String debugMessage;

    /**
     * Sub errors. If more than one error occurred.
     */
    private @Setter List<? extends UserApiSubError> subErrors;


    UserApiError(){
        this.timestamp = LocalDateTime.now();
        this.message = "Unexpected error";
    }

    UserApiError(HttpStatus status) {
        this();
        this.status = status;
    }

    UserApiError(HttpStatus status, Throwable ex) {
        this(status);
        this.debugMessage = ex.getLocalizedMessage();
    }

    UserApiError(HttpStatus status, String message, Throwable ex) {
        this(status, ex);
        this.message = message;
    }

}
