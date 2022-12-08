package com.ay.exchange.common.util;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CharacterValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CharacterConstraint {
    String message() default "Invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}