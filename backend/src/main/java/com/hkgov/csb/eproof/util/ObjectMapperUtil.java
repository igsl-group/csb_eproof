package com.hkgov.csb.eproof.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.hkgov.csb.eproof.exception.GenericException;

import java.util.HashMap;
import java.util.Map;

import static com.hkgov.csb.eproof.exception.ExceptionConstants.READ_VALUE_EXCEPTION_CODE;
import static com.hkgov.csb.eproof.exception.ExceptionConstants.READ_VALUE_EXCEPTION_MESSAGE;


public class ObjectMapperUtil extends ObjectMapper {

    public ObjectMapperUtil() {
        super();
        this.registerModule(new ParameterNamesModule());
        this.registerModule(new Jdk8Module());
        this.registerModule(new JavaTimeModule());
    }

    public <T> T readValue(String content, TypeReference<T> valueTypeRef) {
        try {
            return super.readValue(content, valueTypeRef);
        } catch (JsonProcessingException e) {
            throw new GenericException(READ_VALUE_EXCEPTION_CODE, READ_VALUE_EXCEPTION_MESSAGE, e);
        }
    }

    public <T> T readValue(String content, Class<T> valueType) {
        try {
            return super.readValue(content, valueType);
        } catch (JsonProcessingException e) {
            throw new GenericException(READ_VALUE_EXCEPTION_CODE, READ_VALUE_EXCEPTION_MESSAGE, e);
        }
    }

    public Map<String, Object> convertContextToMap(String context) {
        return readValue(context, HashMap.class);
    }
}
