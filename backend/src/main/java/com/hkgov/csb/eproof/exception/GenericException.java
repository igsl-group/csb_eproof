package com.hkgov.csb.eproof.exception;

public class GenericException extends RuntimeException {
    private final String code;
    private final String message;
    private String field;
    private String value;

    public static class Builder {
        private String code;
        private String message;
        private String field;
        private String value;
        private Throwable cause;

        public static Builder create() {
            return new Builder();
        }

        public GenericException build() {
            return new GenericException(code, message, field, value, cause);
        }

        public Builder setCode(final String code) {
            this.code = code;
            return this;
        }

        public Builder setMessage(final String message) {
            this.message = message;
            return this;
        }

        public Builder setField(final String field) {
            this.field = field;
            return this;
        }

        public Builder setValue(final String value) {
            this.value = value;
            return this;
        }

        public Builder setCause(Throwable cause) {
            this.cause = cause;
            return this;
        }
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
        this.message = message;
        this.field = field;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
