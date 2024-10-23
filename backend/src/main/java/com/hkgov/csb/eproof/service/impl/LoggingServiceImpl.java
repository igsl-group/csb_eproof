package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.service.LoggingService;
import com.hkgov.csb.eproof.util.HttpUtils;
import com.hkgov.csb.eproof.util.ObjectMapperUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class LoggingServiceImpl implements LoggingService {

    private final Logger logger = LoggerFactory.getLogger(LoggingServiceImpl.class);

    private final ObjectMapperUtil objectMapper;

    public LoggingServiceImpl(ObjectMapperUtil objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void logRequest(HttpServletRequest request, Object body) {
        StringBuilder reqMessage = new StringBuilder();
        Map<String, String> parameters = HttpUtils.getParameters(request);
        reqMessage.append("method = [").append(request.getMethod()).append("] ");
        reqMessage.append("path = [").append(request.getRequestURI()).append("] ");
        if (!parameters.isEmpty()) {
            reqMessage.append("parameters = [").append(parameters).append("] ");
        }
        if (Objects.nonNull(body)) {
            reqMessage.append("body = [").append(body).append("] ");
        }

        logger.info("REQUEST: {}", reqMessage);
    }


    @Override
    public void logResponse(HttpServletRequest request, HttpServletResponse response, Object body, long elapsedTime) {
        StringBuilder respMessage = new StringBuilder();
        Map<String, String> headers = HttpUtils.getResponseHeaders(response);
        respMessage.append("method = [").append(request.getMethod()).append("] ");
        respMessage.append("path = [").append(request.getRequestURI()).append("] ");
        respMessage.append("elapsedTime = ").append(elapsedTime).append("ms ");
        respMessage.append("status = [").append(response.getStatus()).append("] ");
//        if (!headers.isEmpty()) {
//            respMessage.append("responseHeaders = [").append(headers).append("] ");
//        }
//        try {
//            respMessage.append("responseBody = [").append(objectMapper.writeValueAsString(body)).append("] ");
//        } catch (Exception e) {
//            respMessage.append("responseBody = [").append("failed to convert body").append("] ");
//        }

        logger.info("RESPONSE: {}", respMessage);
    }
}

