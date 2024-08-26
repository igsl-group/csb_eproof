package com.hkgov.csb.eproof.event;

import hk.gov.spica_scopes.common.jaxb.ScopesFault;
import hk.gov.spica_scopes.common.utils.encoder.EncoderUtils;
import hk.gov.spica_scopes.spica.jaxb.notisender.Attachment;
import hk.gov.spica_scopes.spica.jaxb.notisender.NotiStatus;
import hk.gov.spica_scopes.spica.jaxb.notisender.Recipient;
import hk.gov.spica_scopes.spica.notification.client.restful.NotificationRestfulClient;
import hk.gov.spica_scopes.common.client.PropertyNames;
import org.springframework.util.AntPathMatcher;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import com.hkgov.csb.eproof.CsbEproofApplication;
import com.hkgov.csb.eproof.entity.EmailEvent;
import com.hkgov.csb.eproof.service.EmailService;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class EmailEventPublisher {

    private final ApplicationEventPublisher publisher;

    public EmailEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publicEmailEvent(final EmailEvent event) {
        publisher.publishEvent(event);
    }
}
