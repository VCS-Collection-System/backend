
package com.redhat.service.email;

import java.util.List;

import com.redhat.vcs.model.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailService {

    private static final String FAKE_EMAIL = "example.com";

    private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);

    private JavaMailSender emailSender;

    private String defaultFrom;

    private boolean emailEnabled;

    public EmailService(@Value("${vcs.email.from}") String defaultFrom, @Autowired JavaMailSender emailSender) {
        this.defaultFrom = defaultFrom;
        emailEnabled = Boolean.parseBoolean(System.getProperty("vcs.email.enable", "false"));
        this.emailSender = emailSender;
    }
    
    
    private void validate(String ... emailAddresses) {
        for (String s: emailAddresses) {
            if (!Utils.isEmailValid(s)) {
                throw new EmailServiceException("Invalid email address" + s);
            }
        }
    }

    private void sendEmail(String from, String to, String subject, String text) {
        validate(from, to);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try {
            emailSender.send(message);
            LOG.debug("Email successfully sent to {}. Subject: {}", Utils.maskedString(), subject);
        } catch(Exception e) {
            LOG.debug("Error while sending email", e);
            throw new EmailServiceException("Error while sending email", e);
        }
    }

    public void sendEmail(String to, String subject, String text) {
        if (!emailEnabled) {
            LOG.warn("Email notification is disabled. Set vcs.email.enable=true to enable it.");
        } else if (to.toLowerCase().endsWith(FAKE_EMAIL)) {
            LOG.warn("Email notification not sent due to test email address.");
        } else {
            sendEmail(defaultFrom, to, subject, text);
        }
    }

    public void sendEmail(List<String> recipients, String subject, String text) {
        if (recipients != null) {
            recipients.forEach(to -> sendEmail(to, subject, text));
        }
    }
}
