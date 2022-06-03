package com.redhat.service.cmd;

import java.util.List;

import com.redhat.service.SpringContextConfig;
import com.redhat.service.email.EmailService;

import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailNotificationCommand implements Command {

    private static final Logger LOG = LoggerFactory.getLogger(EmailNotificationCommand.class);

    private EmailService emailService;

    public EmailNotificationCommand() {
        this.emailService = SpringContextConfig.getBean(EmailService.class);
    }

    @Override
    public ExecutionResults execute(CommandContext ctx) throws Exception {
        String subject = (String) ctx.getData("subject");
        String text    = (String) ctx.getData("text");
        LOG.debug(">>> Subject: {}\n>>> Text: {}", subject, text);
        List<String> recipients = (List<String>) ctx.getData("recipients");

        emailService.sendEmail(recipients, subject, text);

        LOG.debug("Email notification has been sent to {} recipient(s)", recipients.size());
        return new ExecutionResults();
    }
}
