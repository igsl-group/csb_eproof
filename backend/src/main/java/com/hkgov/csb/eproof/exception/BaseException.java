package com.hkgov.csb.eproof.exception;

import com.hkgov.csb.eproof.util.MessageUtils;
import com.hkgov.csb.eproof.constants.enums.ResultCode;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * base exception
 *
 * @author Aliven
 */
@Getter
@AllArgsConstructor
public class BaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * module name
     */
    private String module;

    /**
     * error code
     */
    private String code;

    /**
     * error parameters
     */
    private Object[] args;

    /**
     * error message
     */
    private String defaultMessage;

    public BaseException(String module, String code, Object[] args) {
        this(module, code, args, null);
    }

    public BaseException(String module, String defaultMessage) {
        this(module, null, null, defaultMessage);
    }

    public BaseException(String code, Object[] args) {
        this(null, code, args, null);
    }

    public BaseException(String code) {
        this(null, code, null, null);
    }

    @Override
    public String getMessage() {
        String message = null;
        if (!StringUtils.isEmpty(code)) {
            message = MessageUtils.message(ResultCode.getValue(code).getMsg(), args);
        }
        if (message == null) {
            message = defaultMessage;
        }
        return message;
    }

}
