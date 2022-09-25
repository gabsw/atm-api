package com.zinkworks.challenge.atm.validation;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({
    FIELD
})
@Retention(RUNTIME)
@Constraint(validatedBy = AllowedAmount.Validator.class)
@Documented
public @interface AllowedAmount {

    String message() default "Withdrawal amount has to match the available bills";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<AllowedAmount, Integer> {
        private static final int BILLS_LEAST_COMMON_DIVISOR = 5;
        @Override
        public void initialize(final AllowedAmount allowedAmount) {
        }

        @Override
        public boolean isValid(
            final Integer amount,
            final ConstraintValidatorContext constraintValidatorContext) {
            return amount > 0 && amount % BILLS_LEAST_COMMON_DIVISOR == 0;
        }
    }
}
