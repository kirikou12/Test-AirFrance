package fr.airfrance.userdata.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Errors that occur when constraints are not respected.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
class UserApiValidationError extends UserApiSubError {

    /**
     * Name of the object when constraint was violated.
     */
    private String object;

    /**
     * Name of the field when constraint was violated.
     */
    private String field;
    /**
     * Rejected value.
     */
    private Object rejectedValue;

    /**
     * Details of the rejection.
     */
    private String message;

   UserApiValidationError(String object, String message) {
       this.object = object;
       this.message = message;
   }
}