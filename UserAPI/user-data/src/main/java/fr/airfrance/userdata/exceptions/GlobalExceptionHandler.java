package fr.airfrance.userdata.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that handles all services occurred errors.
 */
@ControllerAdvice
@PropertySource(value = "classpath:messages.properties", encoding = "ISO-8859-1")
public class GlobalExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @Value("${ConstraintViolationException.occurred}")
    String constraintViolationOccurred;

    @Value("${MethodArgumentNotValidException.occurred}")
    String methodArgumentNotValidOccurred;

    @Value("InvalidFormatException.occurred")
    String invalidFormatOccurred;


    /**
     * Json bad formatting of a value to deserialize
     * @param ex
     * @return
     */
    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<Object> handleInvalidFormatException(
            InvalidFormatException ex) {

        return buildResponseEntity(new UserApiError(HttpStatus.BAD_REQUEST, this.invalidFormatOccurred, ex));
    }

    /**
     * Handles the custom exceptions thrown by our API.
     * @param ex of type UserNotFoundException
     * @return
     */
    @ExceptionHandler(UserApiException.class)
    public ResponseEntity<Object> handleUserAPIExceptions(
            UserApiException ex) {

        String error = messageSource.getMessage(ex.getMessageKey(), ex.getParams(), LocaleContextHolder.getLocale());
        UserApiError userApiError = new UserApiError(ex.getStatus(), error, ex);

        if(ex.getSubException() != null){
            userApiError.setDebugMessage(ex.getSubException().getLocalizedMessage());
        }

        return buildResponseEntity(userApiError);
    }

    /**
     * Handles the exception that occurs when one or more constraints are violated.
     * @param ex of type ConstraintViolationException
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {


        UserApiError userApiException = new UserApiError(HttpStatus.BAD_REQUEST, constraintViolationOccurred, ex);
        List<UserApiValidationError> userApiValidationErrorList = new ArrayList<>();

        ex.getConstraintViolations().forEach(violation->{
            userApiValidationErrorList.add(new UserApiValidationError(violation.getRootBeanClass().getName(),
                    violation.getPropertyPath().toString(),
                    violation.getInvalidValue(), violation.getMessage()));
        });

        userApiException.setSubErrors(userApiValidationErrorList);

        return buildResponseEntity(userApiException);
    }

    /**
     * Handles exceptions when validation on an argument annotated with @Valid fails.
     * @param ex of type MethodArgumentNotValidException
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        UserApiError userApiException = new UserApiError(HttpStatus.BAD_REQUEST, methodArgumentNotValidOccurred, ex);
        List<UserApiValidationError> userApiValidationErrorList = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach((error) -> {

            userApiValidationErrorList.add(new UserApiValidationError(error.getObjectName(),
                    error.getField(),
                    error.getRejectedValue(), error.getDefaultMessage()));
        });

        userApiException.setSubErrors(userApiValidationErrorList);

        return buildResponseEntity(userApiException);
    }

    /**
     * Creates a ResponseEntity with the API error and its status.
     * @param apiError
     * @return
     */
    private ResponseEntity<Object> buildResponseEntity(UserApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}