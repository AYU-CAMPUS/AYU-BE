package com.ay.exchange.common.util;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class CharacterValidator implements ConstraintValidator<CharacterConstraint, List<String>> {
    private static final String SEPARATOR = ";";
    private static final int LIMIT_LENGTH = 20;

    @Override
    public boolean isValid(List<String> desiredData, ConstraintValidatorContext context) {
        if (desiredData == null) return false;

        for (String data : desiredData) {
            if (data.contains(SEPARATOR) || data.length() > LIMIT_LENGTH) {
                return false;
            }
        }
        return true;
    }
}
