package com.hkgov.csb.eproof.audit.core.spi.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hkgov.csb.eproof.audit.core.spi.AuditRetValResolver;
import com.hkgov.csb.eproof.util.ObjectMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultAuditRetValResolver implements AuditRetValResolver {

    private final Logger logger = LoggerFactory.getLogger(DefaultAuditRetValResolver.class);

    private final ObjectMapperUtil objectMapper;

    public DefaultAuditRetValResolver(ObjectMapperUtil objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String resolveFrom(Object retVal) {
        String result = null;
        try {
            result = (retVal == null) ? null : objectMapper.writeValueAsString(retVal);
        } catch (JsonProcessingException e) {
            logger.error("Failed to convert return value to string", e);
        }
        return result;
    }
}
