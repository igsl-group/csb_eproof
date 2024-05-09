package com.hkgov.csb.eproof.util;;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {
    private String code;
    private String message;
    private boolean success;
    private T data;

    public Result() {
    }

    public Result(String code, String message,boolean success) {
        this.code = code;
        this.message = message;
        this.success = success;
    }

    public Result(String code, String message, T data,boolean success) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
    }


    public String getcode() {
        return code;
    }

    public void setcode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    // 静态工厂方法
    public static <T> Result<T> success(T data) {
        ResultCode rce = ResultCode.SUCCESS;
        boolean success = true;
        if (data instanceof Boolean && Boolean.FALSE.equals(data)) {
            rce = ResultCode.SYSTEM_EXECUTION_ERROR;
            success = false;
        }
        return result(rce,data,success);
    }

    public static <T> Result<T> fail(String code, String message,boolean success) {
        return new Result<>(code, message,success);
    }

    public static <T> Result<T> of(String code, String message, T data,boolean success) {
        return new Result<>(code, message, data,success);
    }
    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> result(ResultCode resultCode, T data,boolean success) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), data,success);
    }
}
