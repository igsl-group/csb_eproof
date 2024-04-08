package com.hkgov.ceo.pms.dto;

import com.hkgov.ceo.pms.validator.ValidDigit;
import com.hkgov.ceo.pms.validator.ValidLength;
import com.hkgov.ceo.pms.validator.ValidLowerCase;
import com.hkgov.ceo.pms.validator.ValidSpecialCase;
import com.hkgov.ceo.pms.validator.ValidUpperCase;
import com.hkgov.ceo.pms.validator.ValidWhitespace;

public class PasswordDto {
    private String oldPassword;
    @ValidDigit
    @ValidLength
    @ValidLowerCase
    @ValidUpperCase
    @ValidSpecialCase
    @ValidWhitespace
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
