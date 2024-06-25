package com.hkgov.csb.eproof.healthCheck;

import com.hkgov.csb.eproof.constants.Constants;
import com.hkgov.csb.eproof.dao.SystemParameterRepository;
import com.hkgov.csb.eproof.service.EmailService;
import com.hkgov.csb.eproof.util.EmailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MailHealthCheck implements ApplicationRunner {

    private final EmailService emailService;
    private final EmailUtil emailUtil;
    private final SystemParameterRepository systemParameterRepository;
    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Value("${mail.health-check.enabled:false}")
    private boolean healthCheckOnStart;

    @Value("${mail.health-check.to}")
    private List<String> healthCheckRecipientList;

    @Value("${mail.health-check.subject}")
    private String healthCheckEmailTitle;

    @Value("${mail.mode}")
    private String mailMode;

    public MailHealthCheck(EmailService emailService, EmailUtil emailUtil, SystemParameterRepository systemParameterRepository) {
        this.emailService = emailService;
        this.emailUtil = emailUtil;
        this.systemParameterRepository = systemParameterRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try{
            if(healthCheckOnStart){
                logger.info("Sending health check email...");
                Map<String,Object> map = new HashMap<>();
                map.put("serverTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_PATTERN)));
                map.put("serverIp", InetAddress.getLocalHost().getHostAddress());
                map.put("mailMode", mailMode);

                String htmlEmailTemplate = systemParameterRepository.findByName(Constants.SYS_PARAM_HEALTH_CHECK_MAIL_TEMPLATE).get().getValue();

                emailService.sendEmail(healthCheckRecipientList,
                        null,
                        null,
                        healthCheckEmailTitle,
                        emailUtil.getRenderedHtml(htmlEmailTemplate,map),
                        null,
                        null);
                logger.info("Health check email sent.");
            }
        } catch(Exception e){
            logger.error("Mail health check failed. Application startup failed.");
            throw e;
        }

    }
}
