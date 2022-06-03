package com.redhat.service.email;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    static final String EMAIL_FLAG_KEY = "vcs.email.enable";
    private static final String DEFAULT_FROM_VALUE = "none@none.com";
    private static final String LEGIT_RECIPIENT = "blah@blah.com";
    private static final String TEST_RECIPIENT = "blah@example.com";
    static final String SUBJECT = "Some Subject";
    private static final String TEXT = "Some text.";

    @Mock
    private JavaMailSender emailSender;

    /**
     * Tests that the EmailService will attempt to send an email when the email flag
     * is enabled and the recipient is NOT example.com.
     */
    @Test
    void testSendEmailFlagEnabledNotTestEmail() {
        System.setProperty(EMAIL_FLAG_KEY, "true");
        EmailService emailService = new EmailService(DEFAULT_FROM_VALUE, emailSender);
        
        emailService.sendEmail(LEGIT_RECIPIENT, SUBJECT, TEXT);
        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    /**
     * Tests that the EmailService will NOT attempt to send an email when the email
     * flag is enabled and the recipient is example.com.
     */
    @Test
    public void testSendEmailFlagEnabledIsTestEmail() {
        System.setProperty(EMAIL_FLAG_KEY, "true");
        EmailService emailService = new EmailService(DEFAULT_FROM_VALUE, emailSender);

        emailService.sendEmail(TEST_RECIPIENT, SUBJECT, TEXT);
        verify(emailSender, times(0)).send(any(SimpleMailMessage.class));
    }

    /**
     * Simple test that confirms that case doesn't matter in the email address
     * check. EXAMPLE.COM is caught just like example.com.
     */
    @Test
    void testSendEmailFlagEnabledIsTestEmailUppercase() {
        System.setProperty(EMAIL_FLAG_KEY, "true");
        EmailService emailService = new EmailService(DEFAULT_FROM_VALUE, emailSender);

        emailService.sendEmail(TEST_RECIPIENT.toUpperCase(), SUBJECT, TEXT);
        verify(emailSender, times(0)).send(any(SimpleMailMessage.class));
    }

    /**
     * Tests that the EmailService will NOT attempt to send an email when the email
     * flag is disabled.
     */
    @Test
    void testSendEmailFlagDisabled() {
        System.setProperty(EMAIL_FLAG_KEY, "false");
        EmailService emailService = new EmailService(DEFAULT_FROM_VALUE, emailSender);

        emailService.sendEmail(LEGIT_RECIPIENT, SUBJECT, TEXT);
        verify(emailSender, times(0)).send(any(SimpleMailMessage.class));
    }
}
