package com.zinkworks.challenge.atm.machine.validation;

import com.zinkworks.challenge.atm.machine.dto.WithdrawalCreate;

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

    String message() default "Withdrawal amount has to match the available bills.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<AllowedAmount, WithdrawalCreate> {
        @Override
        public boolean isValid(
            final WithdrawalCreate withdrawalCreate,
            final ConstraintValidatorContext constraintValidatorContext) {
            final int amount = withdrawalCreate.getAmount();
            return amount > 0 && amount % 5 == 0;
        }
    }
}
