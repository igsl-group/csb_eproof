package com.hkgov.csb.eproof.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hkgov.csb.eproof.dao.UserRepository;
import com.hkgov.csb.eproof.service.EmailService;
import com.hkgov.csb.eproof.service.impl.GcisEmailServiceImpl;
import com.hkgov.csb.eproof.service.impl.SmtpEmailServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.List;

@Configuration
@EnableScheduling
public class ApplicationConfig {

    private final UserRepository userRepository;

    public ApplicationConfig(UserRepository userRepository, SmtpEmailServiceImpl smtpEmailService, GcisEmailServiceImpl gcisEmailService) {
        this.userRepository = userRepository;
        this.smtpEmailService = smtpEmailService;
        this.gcisEmailService = gcisEmailService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }


    @Bean
    public UserDetailsService userDetailsService() {
        return userRepository::getUserByDpUserId;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    private final SmtpEmailServiceImpl smtpEmailService;
    private final GcisEmailServiceImpl gcisEmailService;

    @Value("${mail.mode}")
    private String mailMode;

    @Bean
    public EmailService emailService() {
        if("SMTP".equals(mailMode)){
            return smtpEmailService;
        } else if ("GCIS".equals(mailMode)){
            return gcisEmailService;
        }else {
            throw new IllegalArgumentException("Invalid mail mode");
        }
    }

}
