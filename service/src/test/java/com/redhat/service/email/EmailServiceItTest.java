package com.redhat.service.email;

import static org.junit.Assert.assertTrue;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application_it.properties")
public class EmailServiceItTest {

    private GreenMail mailServer;
    
    @Autowired
    private JavaMailSender emailSender;
    
    @Before
    public void setUp() {
        mailServer = new GreenMail(ServerSetupTest.SMTP)
                .withConfiguration(GreenMailConfiguration.aConfig().withUser("vcs", "some_pass"));
        mailServer.start();
    }
    
    @After
    public void tearDown() {
        mailServer.stop();
    }
    
    /**
     * Mock out a fake email server to confirm that the JavaMailSender is actually sending out emails.
     */
    @Test
    public void emailSendingTest() throws MessagingException {
        System.setProperty(EmailServiceTest.EMAIL_FLAG_KEY, "true");
        EmailService emailService = new EmailService("none@none.com", emailSender);
        emailService.sendEmail("vcs@none.com", EmailServiceTest.SUBJECT, "Some text");

        boolean messageFound = false;
        MimeMessage[] receivedMessages = mailServer.getReceivedMessages();
        for (MimeMessage message : receivedMessages) {
            if(EmailServiceTest.SUBJECT.equals(message.getSubject())) {
                messageFound = true;
                break;
            }
        }
        
        assertTrue(messageFound);
    }
}
