package com.redhat.service.email;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.redhat.service.model.Message;
import com.redhat.service.model.MessageBuilder;

/**
 * Updated according to https://issues.redhat.com/secure/attachment/12646622/12646622_Final+Email+Notifications9-28-21.xlsx
 */
public class EmailTemplates {
    private EmailTemplates() {
        // Empty constructor to prevent instantiation
    }

    public static final String NEGATIVE_RESULT = "NEGATIVE_RESULT";
    public static final String POSITIVE_RESULT_HR = "POSITIVE_RESULT_HR";
    public static final String POSITIVE_RESULT = "POSITIVE_RESULT";
    public static final String INCONCLUSIVE_RESULT = "INCONCLUSIVE_RESULT";
    public static final String VACCINE_PARTIAL = "VACCINE_PARTIAL";
    public static final String VACCINE_UNDER_REVIEW = "VACCINE_UNDER_REVIEW";
    public static final String VACCINE_ACCEPTED = "VACCINE_ACCEPTED";
    public static final String VACCINE_DECLINED = "VACCINE_DECLINED";
    public static final String CERTIFICATE_OF_RECOVERY = "CERTIFICATE_OF_RECOVERY";

    private static final String PEOPLE_TEAM_REQUEST_LINK
            = "https://redhat.service-now.com/help?id=sc_cat_item&sys_id=89e35ece1b4074102d12c880604bcb6a";
    private static final String COVID_19_FAQ_LINK
            = "https://redhat.service-now.com/help?id=kb_search&spa=1&workflow_state=published&kb_knowledge_base=9f106876db5b8890ab8fe8ca4896199c";
    private static final String COVID_19_UPDATES_LINK
            = "https://source.redhat.com/groups/public/cim/covid_19_updates";

    private static final String MESSAGE_ACKNOWLEDGE_SUBMISSION_INFO_AND_DOC
            = "Thank you for submitting your vaccination information and if applicable, supporting documentation.";
    private static final String MESSAGE_ACKNOWLEDGE_SUBMISSION = "Thank you for your submission.";
    private static final String MESSAGE_REVIEW_AND_CONFIRM = "This email is to confirm that we are working to review and confirm the information you submitted.";
    private static final String MESSAGE_TEST_ACKNOWLEDGE_SUBMISSION = "Thank you for your testing submission.";
    private static final String MESSAGE_PEOPLE_HELPDESK_CONTACT
            = "If you have any questions or need assistance, please submit a ticket to the People Helpdesk";
    private static final String MESSAGE_SOURCE_GUIDELINES = "Please continue to follow your specific country safety guidance (including vaccination and testing frequency requirements) detailed on The Source";

    private static final String SUBJECT_VAX_OR_TEST_DOC_RECEIVED = "Testing Documentation Received";
    private static final String SUBJECT_VAX_DOC_RECEIVED = "Vaccination Documentation Received";
    private static final String SUBJECT_VAX_DOC_UNDER_REVIEW = "Vaccination Submission Under Review";
    private static final String SUBJECT_VAX_DOC_ACCEPTED = "Vaccination Documentation Accepted";
    private static final String SUBJECT_VAX_DOC_DECLINED = "Vaccination Documentation Declined";
    private static final String SUBJECT_CERTIFICATE_OF_RECOVERY = "Recovery Documentation Received";

    public static final Message NEGATIVE_RESULT_MSG = new MessageBuilder()
        .id(NEGATIVE_RESULT)
        .subject(SUBJECT_VAX_OR_TEST_DOC_RECEIVED)
        .text(String.join(" ", Arrays.asList(
                MESSAGE_TEST_ACKNOWLEDGE_SUBMISSION,
                MESSAGE_SOURCE_GUIDELINES +"[1]. Please be aware that testing submission frequency and timelines vary by country, and must be received no later than the deadlines described on your country page on The Source to engage in any in-person activity with Red Hatters, customers, or vendors.")))
        .appendText("\n\n")
        .appendText(MESSAGE_PEOPLE_HELPDESK_CONTACT+"[2].")
        .appendText("\n\n")
        .appendText("[1] " + COVID_19_UPDATES_LINK)
        .appendText("\n\n")
        .appendText("[2] " + PEOPLE_TEAM_REQUEST_LINK)
        .build();

    

    public static final Message POSITIVE_RESULT_HR_MSG = new MessageBuilder()
        .id(POSITIVE_RESULT_HR)
        .subject(SUBJECT_VAX_OR_TEST_DOC_RECEIVED)
        .hasParams(true)
        .text(String.join(" ", Arrays.asList(
                "Please note %s has provided documentation for your immediate review.",
                "Please contact the employee for additional information and to provide appropriate guidance.")))
        .build();

    public static final Message POSITIVE_RESULT_MSG = new MessageBuilder()
        .id(POSITIVE_RESULT)
        .subject(SUBJECT_VAX_OR_TEST_DOC_RECEIVED)
        .text(String.join(" ", Arrays.asList(
                MESSAGE_TEST_ACKNOWLEDGE_SUBMISSION,
                "A People team representative will be in touch with you soon to provide additional guidance. Until then, you should not engage in any in-person activities with Red Hatters, customers, or vendors.",
                MESSAGE_SOURCE_GUIDELINES + "[1]")))
        .appendText("\n\n")
        .appendText(MESSAGE_PEOPLE_HELPDESK_CONTACT +"[2].")
        .appendText("\n\n")
        .appendText("[1] " + COVID_19_UPDATES_LINK)
        .appendText("\n\n")
        .appendText("[2] " + PEOPLE_TEAM_REQUEST_LINK)
        .build();

    

    public static final Message INCONCLUSIVE_RESULT_MSG = new MessageBuilder()
        .id(INCONCLUSIVE_RESULT)
        .subject(SUBJECT_VAX_OR_TEST_DOC_RECEIVED)
        .text(String.join(" ", Arrays.asList(
                // TODO: Should the below be MESSAGE_ACKNOWLEDGE_SUBMISSION? (currently a slightly altered version)
                MESSAGE_TEST_ACKNOWLEDGE_SUBMISSION,
                "A People team representative will be in touch with you soon to provide additional guidance. Until then, you should not engage in any in-person activities with Red Hatters, customers, or vendors.If the nature of your work does not permit you to abide by these requirements or you have a business-critical need requiring in-person interactions, please immediately contact the People Helpdesk[1].")))
        .appendText("\n\n")
        .appendText(String.join(" ", Arrays.asList(
                MESSAGE_SOURCE_GUIDELINES+ "[2].",
                "Please be aware that testing submission frequency and timelines vary by country, and must be received no later than the deadlines described on your country page on The Source to engage in an in-person activity with Red Hatters, customers, or vendors.")))
        .appendText("\n\n")
        .appendText(MESSAGE_PEOPLE_HELPDESK_CONTACT + "[1].")
        .appendText("\n\n")
        .appendText("[1] " + PEOPLE_TEAM_REQUEST_LINK)
        .appendText("\n\n")
        .appendText("[2] " + COVID_19_UPDATES_LINK)
        .build();

    public static final Message CERTIFICATE_OF_RECOVERY_MSG = new MessageBuilder()
        .id(CERTIFICATE_OF_RECOVERY)
        .subject(SUBJECT_CERTIFICATE_OF_RECOVERY)
        .text(String.join(" ", Arrays.asList(
                "Thank you for your recovery documentation submission. Based on your work location, you may be required to take additional action after a period of time.",
                "A People team representative will be in touch if additional action is needed.")))
        .appendText("\n\n")
        .appendText(String.join(" ", Arrays.asList(
                MESSAGE_SOURCE_GUIDELINES+ "[1].",
                "Please be aware that testing submission frequency and timelines vary by country, and must be received no later than the deadlines described on your country page on The Source to engage in any in-person activity with Red Hatters, customers, or vendors.")))
        .appendText("\n\n")
        .appendText(MESSAGE_PEOPLE_HELPDESK_CONTACT+"[2]")
        .appendText("\n\n")
        .appendText("[1] " + COVID_19_UPDATES_LINK)
        .appendText("\n\n")
        .appendText("[2] " + PEOPLE_TEAM_REQUEST_LINK)
        .build();

    public static final Message VACCINE_PARTIAL_MSG = new MessageBuilder()
        .id(VACCINE_PARTIAL)
        .subject(SUBJECT_VAX_DOC_RECEIVED)
        .text(String.join(" ", Arrays.asList(
                MESSAGE_ACKNOWLEDGE_SUBMISSION_INFO_AND_DOC,
                MESSAGE_REVIEW_AND_CONFIRM)))
        .appendText("\n\n")
        .appendText(String.join(" ", Arrays.asList(
                "In the meantime, answers to a number of frequently asked questions (FAQ) are available on the ServiceNow COVID-19 knowledge base[1].",
                "If you have a question not answered by these resources, please submit a ticket to the People team[2].")))
        .appendText("\n\n")
        .appendText("[1] " + COVID_19_FAQ_LINK)
        .appendText("\n\n")
        .appendText("[2] " + PEOPLE_TEAM_REQUEST_LINK)
        .build();

    public static final Message VACCINE_UNDER_REVIEW_MSG = new MessageBuilder()
        .id(VACCINE_UNDER_REVIEW)
        .subject(SUBJECT_VAX_DOC_UNDER_REVIEW)
        .text(String.join(" ", Arrays.asList(
                MESSAGE_ACKNOWLEDGE_SUBMISSION_INFO_AND_DOC,
                MESSAGE_REVIEW_AND_CONFIRM)))
        .appendText("\n\n")
        .appendText(String.join(" ", Arrays.asList(
                "Your experience from this point depends on your compliance with the vaccination standard in the country where you are based.",
                "In some countries where Red Hat operates, individuals who have received one dose of a one-dose vaccination regime or two doses of a two-dose vaccination regimen are considered fully vaccinated.",
                "In others, receiving an additional booster shot is required to be considered fully vaccinated.",
                "When you receive the missing dose(s), you are expected to submit this information using the validation check-in system.")))
        .appendText("\n\n")
        .appendText(String.join(" ", Arrays.asList(
                "In all cases, continue to follow Red Hat’s COVID safety requirements and country-specific requirements detailed on The Source[1].",
                "If the nature of your work does not permit you to abide by these requirements, please immediately contact the People Helpdesk[2].",
                "You should also begin conversations with your manager about the situation. Any change in these requirements will be communicated to you directly by the People team.")))
        .appendText("\n\n")
        .appendText(String.join(" ", Arrays.asList(
                "Additional information is available on the ServiceNow Knowledge Base on COVID-19 frequently asked questions[3] (FAQ).",
                "Additional questions can be raised through the People Helpdesk[2].")))
        .appendText("\n\n")
        .appendText("[1] " + COVID_19_UPDATES_LINK)
        .appendText("\n\n")
        .appendText("[2] " + PEOPLE_TEAM_REQUEST_LINK)
        .appendText("\n\n")
        .appendText("[3] " + COVID_19_FAQ_LINK)
        .build();

    public static final Message VACCINE_ACCEPTED_MSG = new MessageBuilder()
        .id(VACCINE_ACCEPTED)
        .subject(SUBJECT_VAX_DOC_ACCEPTED)
        .text(String.join(" ", Arrays.asList(
                MESSAGE_ACKNOWLEDGE_SUBMISSION,
                "Your vaccination information has been reviewed and accepted.",
                "You are in compliance with Red Hat’s vaccination requirement in the country where you are based.",
                "To engage in in-person activities, follow Red Hat’s COVID safety requirements and country-specific requirements detailed on The Source[1].")))
        .appendText("\n\n")
        .appendText("You are not required to submit any additional information through the validation check-in system at this time.")
        .appendText("\n\n")
        .appendText("If you have any questions or need assistance, please submit a ticket to the People Helpdesk[2].")
        .appendText("\n\n")
        .appendText("[1] " + COVID_19_UPDATES_LINK)
        .appendText("\n\n")
        .appendText("[2] " + PEOPLE_TEAM_REQUEST_LINK)
        .build();

    public static final Message VACCINE_DECLINED_MSG = new MessageBuilder()
        .id(VACCINE_DECLINED)
        .subject(SUBJECT_VAX_DOC_DECLINED)
        .hasParams(true)
        .text(String.join(" ", Arrays.asList(
                MESSAGE_ACKNOWLEDGE_SUBMISSION,
                "Please be advised that a component of your submission was not accepted.",
                "Your submission was declined because %s.",
                "You should take immediate action to rectify this issue by reviewing the requirements below and resubmitting your vaccination information.")))
        .appendText("\n\n")
        .appendText(String.join(" ", Arrays.asList(
                "This article provides more information on acceptable proof and format of vaccination documentation [1].")))
        .appendText("\n\n")
        .appendText(String.join(" ", Arrays.asList(
                "This article provides details on what information should be included in your proof of vaccination[2].")))
        .appendText("\n\n")
        .appendText(String.join(" ", Arrays.asList(
                "We are here to work with you to ensure that your valid information and supporting documentation are accepted by the system.")))
        .appendText("\n\n")
        .appendText(String.join(" ", Arrays.asList(
                "Until you receive confirmation that your vaccination submission has been approved, you should not come into in-person contact with any Red Hatters, customers, or vendors.",
                "In all cases, continue to follow Red Hat’s COVID safety requirements and country-specific requirements detailed on The Source[3].",
                "Immediately contact the People Helpdesk if the nature of your work does not permit you to abide by these requirements[4].",
                "Any change in these requirements will be communicated to you directly by the People team.")))
        .appendText("\n\n")
        .appendText("If you have any questions or need assistance, please submit a ticket to the People Helpdesk[3].")
        .appendText("\n\n")
        .appendText("[1] https://redhat.service-now.com/help?id=kb_article_view&sysparm_article=KB0015355")
        .appendText("\n\n")
        .appendText("[2] https://redhat.service-now.com/help?id=kb_article_view&sysparm_article=KB0015848")
        .appendText("\n\n")
        .appendText("[3] " + COVID_19_UPDATES_LINK)
        .appendText("\n\n")
        .appendText("[4] " + PEOPLE_TEAM_REQUEST_LINK)
        .build();

    private static final Map<String, Message> cache = new HashMap<>();

    static {
        cache.put(NEGATIVE_RESULT_MSG.getId(), NEGATIVE_RESULT_MSG);
        cache.put(POSITIVE_RESULT_HR_MSG.getId(), POSITIVE_RESULT_HR_MSG);
        cache.put(POSITIVE_RESULT_MSG.getId(), POSITIVE_RESULT_MSG);
        cache.put(INCONCLUSIVE_RESULT_MSG.getId(), INCONCLUSIVE_RESULT_MSG);
        cache.put(VACCINE_PARTIAL_MSG.getId(), VACCINE_PARTIAL_MSG);
        cache.put(VACCINE_UNDER_REVIEW_MSG.getId(), VACCINE_UNDER_REVIEW_MSG);
        cache.put(VACCINE_ACCEPTED_MSG.getId(), VACCINE_ACCEPTED_MSG);
        cache.put(VACCINE_DECLINED_MSG.getId(), VACCINE_DECLINED_MSG);
        cache.put(CERTIFICATE_OF_RECOVERY_MSG.getId(), CERTIFICATE_OF_RECOVERY_MSG);
    }

    public static Message get(String key) {
        return cache.get(key);
    }
}
