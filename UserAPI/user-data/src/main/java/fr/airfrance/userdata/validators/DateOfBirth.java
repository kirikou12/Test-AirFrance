package fr.airfrance.userdata.validators;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.LocalDate;
import java.time.Period;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Makes sure that a user age is > 18
 */
@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = DateOfBirthValidator.class)
@Documented
public @interface DateOfBirth {

    String message() default "Invalid date of birth.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

class DateOfBirthValidator implements ConstraintValidator<DateOfBirth, LocalDate> {


    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {

        if(value==null) return false;

        return Period.between(value, LocalDate.now()).getYears() > 18 ;

    }
}