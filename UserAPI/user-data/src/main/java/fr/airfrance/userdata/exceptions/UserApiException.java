package fr.airfrance.userdata.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Thrown when any UserAPI related error occurs.
 */
@Getter
@Setter
public class UserApiException extends Exception{

    private final String messageKey;
    private final Object[] params;
    private final Exception subException;
    private HttpStatus status;

    public UserApiException(String messageKey){
        this(messageKey, null);
    }

    public UserApiException(String messageKey, Object[] params){
        this(messageKey, params, HttpStatus.BAD_REQUEST);
    }

    public UserApiException(String messageKey, Object[] params, HttpStatus status){
        this(messageKey, params, status, null);
    }

    public UserApiException(String messageKey, Object[] params, HttpStatus status, Exception exception){
        this.messageKey = messageKey;
        this.params = params;
        this.status = status;
        this.subException = exception;
    }
}
