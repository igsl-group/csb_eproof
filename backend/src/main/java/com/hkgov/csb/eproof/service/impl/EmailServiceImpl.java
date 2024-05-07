package com.hkgov.csb.eproof.service.impl;


import com.hkgov.csb.eproof.dao.EmailEventRepository;
import com.hkgov.csb.eproof.dao.EmailTemplateRepository;
import com.hkgov.csb.eproof.entity.EmailContext;
import com.hkgov.csb.eproof.entity.EmailEvent;
import com.hkgov.csb.eproof.entity.EmailMessage;
import com.hkgov.csb.eproof.entity.EmailTemplate;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.service.EmailService;
import com.hkgov.csb.eproof.util.ObjectMapperUtil;
import com.hkgov.csb.eproof.exception.ExceptionConstants;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static com.hkgov.csb.eproof.config.Constants.SEPARATOR;


@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;

    private final SpringTemplateEngine thymeleafTemplateEngine;

    private final EmailEventRepository emailEventRepository;

    private final EmailTemplateRepository emailTemplateRepository;

    private final ObjectMapperUtil objectMapper;

    @Value("${spring.mail.from}")
    private String emailFrom;

    @Value("${email.extend.list}")
    private String emailExtendList;

    public EmailServiceImpl(JavaMailSender emailSender, SpringTemplateEngine thymeleafTemplateEngine, EmailEventRepository emailEventRepository, EmailTemplateRepository emailTemplateRepository, ObjectMapperUtil objectMapper) {
        this.emailSender = emailSender;
        this.thymeleafTemplateEngine = thymeleafTemplateEngine;
        this.emailEventRepository = emailEventRepository;
        this.emailTemplateRepository = emailTemplateRepository;
        this.objectMapper = objectMapper;
    }

    private String convertContextToHtmlBody(String body, String context, Locale locale) {
        Context thymeleafContext = new Context();
        Map<String, Object> templateModel = objectMapper.convertContextToMap(context);
        thymeleafContext.setLocale(locale);
        thymeleafContext.setVariables(templateModel);
        return thymeleafTemplateEngine.process(body, thymeleafContext);
    }

    private String convertContextToEmailSubject(String subject, String context, Locale locale) {
        Context thymeleafContext = new Context();
        Map<String, Object> templateModel = objectMapper.convertContextToMap(context);
        thymeleafContext.setLocale(locale);
        thymeleafContext.setVariables(templateModel);
        return thymeleafTemplateEngine.process(subject, thymeleafContext);
    }

    @Override
    public void sendEmail(EmailEvent event) throws MessagingException {
        MimeMessageHelper helper;
        Authentication authentication = new UsernamePasswordAuthenticationToken(event.getCreatedBy(), null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        MimeMessage message = emailSender.createMimeMessage();
        EmailMessage emailMessage = Optional.ofNullable(event.getEmailMessage()).orElseThrow(EntityNotFoundException::new);
        EmailTemplate emailTemplate = Optional.ofNullable(emailMessage.getEmailTemplate()).orElseThrow(EntityNotFoundException::new);
        helper = new MimeMessageHelper(message, false, "UTF-8");
        helper.setFrom(emailFrom);
        helper.setTo(getSendTo(emailMessage));

        String[] sendCc = getSendCc(emailMessage);
        if (sendCc != null)
            helper.setCc(sendCc);
        helper.setSubject(convertContextToEmailSubject(emailTemplate.getSubject(), emailMessage.getContext(), Locale.forLanguageTag("en")));
        String htmlBody = convertContextToHtmlBody(emailTemplate.getBody(), emailMessage.getContext(), Locale.forLanguageTag("en"));
        helper.setText(htmlBody, true);
        emailSender.send(message);
    }

    @Override
    @Transactional
    public EmailEvent createEmailEvent(String emailTo, String emailCc, String templateId, String context) {
        return emailEventRepository.save(prepareEmailEvent(emailTo, emailCc, templateId, context));
    }

    private EmailEvent prepareEmailEvent(String emailTo, String emailCc, String templateCode, String context) {
        EmailEvent event = new EmailEvent();
        EmailMessage message = new EmailMessage();
        EmailTemplate template = emailTemplateRepository.findByEmailTemplateCode(templateCode).orElseThrow(EntityNotFoundException::new);
        message.setEmailTemplate(template);
        message.setEmailTo(prepareEmailTo(emailTo));
        message.setEmailCc(emailCc);
        message.setContext(context);
        event.setEmailMessage(message);
        return event;
    }


    @Override
    public String convertContextToJson(EmailContext context) {
        try {
            return objectMapper.writeValueAsString(context);
        } catch (Exception e) {

            throw new GenericException(ExceptionConstants.EMAIL_CONTEXT_CONVERT_EXCEPTION_CODE, ExceptionConstants.EMAIL_CONTEXT_CONVERT_EXCEPTION_MESSAGE);
        }
    }

    private String prepareEmailTo(String emailTo) {
        if (StringUtils.isNotEmpty(emailExtendList)) {
            return emailTo + emailExtendList;
        }
        return emailTo;
    }

    private String[] getSendTo(EmailMessage emailMessage) {
        return Optional.ofNullable(emailMessage)
                .map(EmailMessage::getEmailTo)
                .filter(StringUtils::isNotEmpty)
                .map(email -> email.split(SEPARATOR))
                .orElseThrow(() -> new GenericException(ExceptionConstants.EMAIL_TO_LIST_INVALID_EXCEPTION_CODE, ExceptionConstants.EMAIL_TO_LIST_INVALID_EXCEPTION_MESSAGE));
    }

    private String[] getSendCc(EmailMessage emailMessage) {
        return Optional.ofNullable(emailMessage)
                .map(EmailMessage::getEmailCc)
                .filter(StringUtils::isNotEmpty)
                .map(email -> email.split(SEPARATOR))
                .orElse(null);
    }
}
