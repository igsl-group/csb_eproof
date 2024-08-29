package com.hkgov.csb.eproof.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendEmailRequest {
    String to;
    String cc;
    String title;
    String htmlBody;
}

