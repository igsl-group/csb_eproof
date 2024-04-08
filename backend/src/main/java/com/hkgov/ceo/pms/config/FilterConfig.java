package com.hkgov.ceo.pms.config;

import com.hkgov.ceo.pms.audit.common.web.ClientInfoThreadLocalFilter;
import com.hkgov.ceo.pms.filter.AuthenticatedInfoFilter;
import com.hkgov.ceo.pms.filter.ContentCachingFilter;
import com.hkgov.ceo.pms.filter.RequestLogFilter;
import com.hkgov.ceo.pms.service.AuthenticatedInfoService;
import com.hkgov.ceo.pms.service.LoggingService;
import com.hkgov.ceo.pms.service.UserService;
import com.hkgov.ceo.pms.util.HttpUtils;
import com.hkgov.ceo.pms.validator.UserValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
public class FilterConfig {

    private final UserService userService;
    private final AuthenticatedInfoService authenticatedInfoService;
    private final LoggingService loggingService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final HttpUtils httpUtils;
    private final UserValidator userValidator;
    @Value("${filter.config.urlPatterns}")
    private String[] urlPatterns;

    public FilterConfig(UserService userService, AuthenticatedInfoService authenticatedInfoService,
                        LoggingService loggingService,
                        HandlerExceptionResolver handlerExceptionResolver , HttpUtils httpUtils,
                        UserValidator userValidator) {
        this.userService = userService;
        this.authenticatedInfoService = authenticatedInfoService;
        this.loggingService = loggingService;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.httpUtils = httpUtils;
        this.userValidator = userValidator;
    }

    @Bean
    public FilterRegistrationBean<ContentCachingFilter> contentCachingFilterRegistration() {
        FilterRegistrationBean<ContentCachingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ContentCachingFilter());
        registration.addUrlPatterns(urlPatterns);
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<RequestLogFilter> requestLogFilterRegistration() {
        FilterRegistrationBean<RequestLogFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RequestLogFilter(loggingService, httpUtils));
        registration.addUrlPatterns(urlPatterns);
        registration.setOrder(2);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<AuthenticatedInfoFilter> authenticatedInfoFilterRegistration() {
        FilterRegistrationBean<AuthenticatedInfoFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new AuthenticatedInfoFilter(authenticatedInfoService, userService, handlerExceptionResolver, userValidator));
        registration.addUrlPatterns(urlPatterns);
        registration.setOrder(3);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<ClientInfoThreadLocalFilter> clientInfoThreadLocalFilterRegistration() {
        FilterRegistrationBean<ClientInfoThreadLocalFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ClientInfoThreadLocalFilter());
        registration.addUrlPatterns(urlPatterns);
        registration.addInitParameter(ClientInfoThreadLocalFilter.CONST_IP_ADDRESS_HEADER, "X-Real-IP");
        registration.setOrder(4);
        return registration;
    }

}
