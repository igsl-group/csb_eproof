
package com.hkgov.csb.eproof.event;

import com.hkgov.csb.eproof.entity.EmailEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

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

