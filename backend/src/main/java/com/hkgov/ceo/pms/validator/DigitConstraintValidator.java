package com.hkgov.ceo.pms.validator;

import com.google.common.base.Joiner;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;


public class DigitConstraintValidator implements ConstraintValidator<ValidDigit, String> {
    @Override
    public void initialize(ValidDigit arg0) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        PasswordValidator validator = new PasswordValidator(new CharacterRule(EnglishCharacterData.Digit, 1));

        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                        Joiner.on(",").join(validator.getMessages(result)))
                .addConstraintViolation();
        return false;
    }
}