package com.hkgov.csb.eproof.exception;

import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenericException extends RuntimeException {
    private final String code;
    private final String message;


    public GenericException(){
        this.code = ExceptionEnums.SYSTEM_ERROR.getCode();
        this.message = ExceptionEnums.SYSTEM_ERROR.getMessage();
    }
    public GenericException (ExceptionEnums exceptionEnums){
        this.code = exceptionEnums.getCode();
        this.message = exceptionEnums.getMessage();
    }
    public GenericException(String code, String message, Throwable cause) {
        super(cause);
        this.code = code;
        this.message = message;
    }

    public GenericException(String code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    private GenericException(String code, String message, String field, String value, Throwable cause) {
        super(cause);
        this.code = code;
        this.message = message;}

}
