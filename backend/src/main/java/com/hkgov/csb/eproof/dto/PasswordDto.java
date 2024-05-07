package com.hkgov.csb.eproof.dto;

import com.hkgov.csb.eproof.validator.ValidDigit;
import com.hkgov.csb.eproof.validator.ValidLength;
import com.hkgov.csb.eproof.validator.ValidLowerCase;
import com.hkgov.csb.eproof.validator.ValidSpecialCase;
import com.hkgov.csb.eproof.validator.ValidUpperCase;
import com.hkgov.csb.eproof.validator.ValidWhitespace;

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
