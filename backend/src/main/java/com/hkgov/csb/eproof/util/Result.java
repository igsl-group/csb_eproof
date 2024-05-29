package com.hkgov.csb.eproof.util;;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.hkgov.csb.eproof.constants.enums.ResultCode;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {


    private static final long serialVersionUID = -9037821067418468060L;
    private String code;

    private T data;

    private String message;

    private Object[] messageArgs;

    private List<Map<String, String>> validFields;

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        ResultCode rce = ResultCode.SUCCESS;
        if (data instanceof Boolean && Boolean.FALSE.equals(data)) {
            rce = ResultCode.SYSTEM_EXECUTION_ERROR;
        }
        return result(rce, data);
    }


    public static <T> Result<T> success(T data, Long total) {
        Result<T> result = new Result();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMsg());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> failed() {
        return result(ResultCode.SYSTEM_EXECUTION_ERROR.getCode(), ResultCode.SYSTEM_EXECUTION_ERROR.getMsg(), null, null, null);
    }

    public static <T> Result<T> failed(String msg) {
        return result(ResultCode.SYSTEM_EXECUTION_ERROR.getCode(), msg, null, null, null);
    }

    public static <T> Result<T> failed(String code, String msg) {
        return result(code, msg, null, null, null);
    }

    public static <T> Result<T> judge(boolean status) {
        if (status) {
            return success();
        } else {
            return failed();
        }
    }

    public static <T> Result<T> failed(ResultCode resultCode) {
        return result(resultCode.getCode(), resultCode.getMsg(), null, null, null);
    }

    public static <T> Result<T> failed(ResultCode resultCode, Object... messageArgs) {
        return result(resultCode.getCode(), resultCode.getMsg(), null, null, messageArgs);
    }

    public static <T> Result<T> result(ResultCode resultCode, T data) {
        return result(resultCode.getCode(), resultCode.getMsg(), data, null, null);
    }

    public static <T> Result<T> result(ResultCode resultCode) {
        return result(resultCode.getCode(), resultCode.getMsg(), null, null, null);
    }

    public static <T> Result<T> result(ResultCode resultCode, String msg) {
        return result(resultCode.getCode(), msg, null, null, null);
    }

    public static <T> Result<T> result(String code, String msg, T data, List<Map<String, String>> valida, Object[] args) {
        Result<T> result = new Result<T>();
        result.setCode(code);
        result.setData(data);
        result.setMessage(StrUtil.format(" {}", Objects.isNull(args) ? MessageUtils.message(msg) : MessageUtils.message(msg, args)));
        result.setValidFields(valida);
        return result;
    }


    public static boolean isSuccess(Result result) {
        return result != null && ResultCode.SUCCESS.getCode().equals(result.getCode());
    }
}
