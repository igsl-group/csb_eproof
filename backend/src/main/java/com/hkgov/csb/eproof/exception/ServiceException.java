package com.hkgov.csb.eproof.exception;


import com.hkgov.csb.eproof.constants.enums.ResultCode;

/**
 * Servcie Exception
 *
 */
public class ServiceException extends BaseException {

    public ServiceException(String module, String code, Object[] args, String defaultMessage) {
        super(module, code, args, defaultMessage);
    }

    public ServiceException(String module, String code, Object[] args) {
        super(module, code, args);
    }

    public ServiceException(String module, String defaultMessage) {
        super(module, defaultMessage);
    }

    public ServiceException(String code, Object[] args) {
        super(code, args);
    }

    public ServiceException(String defaultMessage) {
        super(null, defaultMessage);
    }

    public ServiceException(ResultCode code, Object... args) {
        super(code.getCode(), args);
    }

    public ServiceException(ResultCode code) {
        super(code.getCode());
    }
}

