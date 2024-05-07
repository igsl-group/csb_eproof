package com.hkgov.csb.eproof.service;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface LoggingService {

    void logRequest(HttpServletRequest request, Object body);

    void logResponse(HttpServletRequest request, HttpServletResponse response, Object body, long elapsedTime);
}
