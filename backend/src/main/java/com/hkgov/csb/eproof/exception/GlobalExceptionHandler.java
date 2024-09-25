package com.hkgov.csb.eproof.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import static com.hkgov.csb.eproof.exception.ExceptionConstants.JWT_TOKEN_EXPIRY_EXCEPTION_CODE;
import static com.hkgov.csb.eproof.exception.ExceptionConstants.JWT_TOKEN_EXPIRY_EXCEPTION_MESSAGE;
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  //  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ObjectMapper objectMapper;
    private static final String CODE = "code";
    private static final String MESSAGE = "message";

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(GenericException.class)
    public ProblemDetail handleCustomException(GenericException ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setProperty(CODE, ex.getCode());
        pd.setProperty(MESSAGE, ex.getMessage());
//        pd.setProperty("field", ex.getField());
//        pd.setProperty("value", ex.getValue());
        pd.setProperty("detailMessage", Optional.ofNullable(ex.getCause())
                .map(Throwable::getMessage)
                .orElse(null));
        logger.error("Exception caught: ", ex);
        return pd;
    }

    @ExceptionHandler(ServiceException.class)
    public ProblemDetail handleServiceException(ServiceException ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setProperty(CODE, ex.getCode());
        pd.setProperty(MESSAGE, ex.getMessage());
//        pd.setProperty("field", ex.getField());
//        pd.setProperty("value", ex.getValue());
        pd.setProperty("detailMessage", Optional.ofNullable(ex.getCause())
                .map(Throwable::getMessage)
                .orElse(null));
        logger.error("Exception caught: ", ex);
        return pd;
    }

    @ExceptionHandler(SQLException.class)
    public ProblemDetail handleGenericException(SQLException ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setProperty(CODE, ex.getErrorCode());
        if (ex.getErrorCode() == 1062) {
            pd.setProperty(MESSAGE, "Code already exists");
            pd.setProperty("detail", ex.getMessage());
        } else {
            pd.setProperty(MESSAGE, ex.getMessage());
        }
        logger.error("Exception caught: ", ex);
        return pd;
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Object> handleJwtException(Exception ex, WebRequest request) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", Calendar.getInstance().getTime());
        data.put("exception", ex.getMessage());
        data.put(MESSAGE, JWT_TOKEN_EXPIRY_EXCEPTION_MESSAGE);
        data.put(CODE, JWT_TOKEN_EXPIRY_EXCEPTION_CODE);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        logger.error("Exception caught: ", ex);
        return new ResponseEntity<>(objectMapper.writeValueAsString(data), headers, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e, WebRequest request) {
        List<ProblemDetail> result = e.getConstraintViolations()
                .stream()
                .map(violation -> {
                    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
                    pd.setProperty(MESSAGE, violation.getMessage());
                    pd.setProperty("field", violation.getPropertyPath().toString());
                    return pd;
                }).toList();
        logger.error("Exception caught: ", e);
        return handleExceptionInternal(e, result,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(LoginException.class)
    public ProblemDetail handleLoginException(LoginException ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        pd.setProperty(CODE, ex.getCode());
        pd.setProperty(MESSAGE, ex.getMessage());
        pd.setProperty("field", ex.getField());
        pd.setProperty("value", ex.getValue());
        logger.error("Exception caught: ", ex);
        return pd;
    }

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleRuntimeException(RuntimeException ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setProperty(CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
        pd.setProperty(MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        pd.setProperty("detailMessage", Optional.ofNullable(ex.getCause())
                .map(Throwable::getMessage)
                .orElse(null));
        logger.error("Exception caught: ", ex);
        return pd;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleThrowException(Exception ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setProperty(CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
        pd.setProperty(MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        pd.setProperty("detailMessage", Optional.ofNullable(ex.getCause())
                .map(Throwable::getMessage)
                .orElse(null));
        logger.error("Exception caught: ", ex);
        return pd;
    }
}
