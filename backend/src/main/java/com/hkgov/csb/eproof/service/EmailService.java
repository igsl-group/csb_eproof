package com.hkgov.csb.eproof.service;

import jakarta.mail.MessagingException;

import java.util.List;

public interface EmailService {

    /**
     * One email will be sent to all applicant
     * @param to list of recipient email address
     * @param cc list of cc email address
     * @param bcc list of bcc email address
     * @param subject subject of the email
     * @param content HTML content of the email
     * @param attachmentName name of the attachment
     * @param attachment attachment of the email in byte array format
     */
    void sendEmail(List<String> to,
                   List<String> cc,
                   List<String> bcc,
                   String subject,
                   String content,
                   String attachmentName,
                   byte [] attachment) throws MessagingException;

    void sendBatchEmail(List<String> to,
                        String subject,
                        String content,
                        String attachmentName,
                        byte [] attachment) throws MessagingException;
}
