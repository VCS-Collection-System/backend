package com.redhat.service.email;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.redhat.service.model.Message;

import org.junit.jupiter.api.Test;

/**
 * This class contains the unit (logic / flow) tests for EmailTemplates.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 */
public class EmailTemplatesTest {

    /**
     * This "equals(Object)" test is a happy path validation, for the following condition(s):
     *     - Both objects are the same reference (i.e., obj1 == obj2)
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a boolean value which:
     *     - Is "true"
     */
    @Test
    public void test_get_success_negativeResultMessage() {
        // Set up data
        String ID = EmailTemplates.NEGATIVE_RESULT;

        String EXPECTED_MESSAGE = "Thank you for your testing submission. Please continue to follow your specific country safety guidance (including vaccination and testing frequency requirements) detailed on The Source[1]. Please be aware that testing submission frequency and timelines vary by country, and must be received no later than the deadlines described on your country page on The Source to engage in any in-person activity with Red Hatters, customers, or vendors."
                + "\n\n"
                + "If you have any questions or need assistance, please submit a ticket to the People Helpdesk[2]."
                + "\n\n"
                + "[1] https://source.redhat.com/groups/public/cim/covid_19_updates"
                + "\n\n"
                + "[2] https://redhat.service-now.com/help?id=sc_cat_item&sys_id=89e35ece1b4074102d12c880604bcb6a";

        // Set up mocks

        // Run test
        Message message = EmailTemplates.get(ID);

        // Validate
        assertNotNull(message);

        assertEquals(
                ID,
                message.getId(),
                "Incorrect message ID on the retrieved EmailTemplate");

        assertEquals(
                "Testing Documentation Received",
                message.getSubject(),
                "Incorrect subject on the retrieved EmailTemplate");

        assertFalse(message.hasParans(), "Message should not have params");

        assertEquals(
                EXPECTED_MESSAGE,
                message.getText(),
                "Incorrect text on the retrieved EmailTemplate");
    }

  

    @Test
    public void test_get_success_positiveResultHRMessage() {
        // Set up data
        String ID = EmailTemplates.POSITIVE_RESULT_HR;

        String EXPECTED_MESSAGE = "Please note Bob Barker has provided documentation for your immediate review. Please contact the employee for additional information and to provide appropriate guidance.";

        // Set up mocks

        // Run test
        Message message = EmailTemplates.get(ID);

        // Validate
        assertNotNull(message);

        assertEquals(
                ID,
                message.getId(),
                "Incorrect message ID on the retrieved EmailTemplate");

        assertEquals(
                "Testing Documentation Received",
                message.getSubject(),
                "Incorrect subject on the retrieved EmailTemplate");

        assertTrue(message.hasParans(), "Message should have params");

        assertEquals(
                EXPECTED_MESSAGE,
                message.getText("Bob Barker"),
                "Incorrect text on the retrieved EmailTemplate");
    }

    @Test
    public void test_get_success_positiveResultMessage() {
        // Set up data
        String ID = EmailTemplates.POSITIVE_RESULT;

        String EXPECTED_MESSAGE = "Thank you for your testing submission. A People team representative will be in touch with you soon to provide additional guidance. Until then, you should not engage in any in-person activities with Red Hatters, customers, or vendors. Please continue to follow your specific country safety guidance (including vaccination and testing frequency requirements) detailed on The Source[1]"
        + "\n\n"
        + "If you have any questions or need assistance, please submit a ticket to the People Helpdesk[2]."
        + "\n\n"
        + "[1] https://source.redhat.com/groups/public/cim/covid_19_updates"
        + "\n\n"
        + "[2] https://redhat.service-now.com/help?id=sc_cat_item&sys_id=89e35ece1b4074102d12c880604bcb6a";

        // Set up mocks

        // Run test
        Message message = EmailTemplates.get(ID);

        // Validate
        assertNotNull(message);

        assertEquals(
                ID,
                message.getId(),
                "Incorrect message ID on the retrieved EmailTemplate");

        assertEquals(
                "Testing Documentation Received",
                message.getSubject(),
                "Incorrect subject on the retrieved EmailTemplate");

        assertFalse(message.hasParans(), "Message should not have params");

        assertEquals(
                EXPECTED_MESSAGE,
                message.getText(),
                "Incorrect text on the retrieved EmailTemplate");
    }

    

    @Test
    public void test_get_success_inconclusiveResultMessage() {
        // Set up data
        String ID = EmailTemplates.INCONCLUSIVE_RESULT;

        String EXPECTED_MESSAGE = "Thank you for your testing submission. A People team representative will be in touch with you soon to provide additional guidance. Until then, you should not engage in any in-person activities with Red Hatters, customers, or vendors.If the nature of your work does not permit you to abide by these requirements or you have a business-critical need requiring in-person interactions, please immediately contact the People Helpdesk[1]."
                + "\n\n"
                +"Please continue to follow your specific country safety guidance (including vaccination and testing frequency requirements) detailed on The Source[2]. Please be aware that testing submission frequency and timelines vary by country, and must be received no later than the deadlines described on your country page on The Source to engage in an in-person activity with Red Hatters, customers, or vendors."
                +"\n\n"
                +"If you have any questions or need assistance, please submit a ticket to the People Helpdesk[1]."
                +"\n\n"
                +"[1] https://redhat.service-now.com/help?id=sc_cat_item&sys_id=89e35ece1b4074102d12c880604bcb6a"
                +"\n\n"
                +"[2] https://source.redhat.com/groups/public/cim/covid_19_updates";

        // Set up mocks

        // Run test
        Message message = EmailTemplates.get(ID);

        // Validate
        assertNotNull(message);

        assertEquals(
                ID,
                message.getId(),
                "Incorrect message ID on the retrieved EmailTemplate");

        assertEquals(
                "Testing Documentation Received",
                message.getSubject(),
                "Incorrect subject on the retrieved EmailTemplate");

        assertFalse(message.hasParans(), "Message should not have params");

        assertEquals(
                EXPECTED_MESSAGE,
                message.getText(),
                "Incorrect text on the retrieved EmailTemplate");
    }

    @Test
    public void test_get_success_certificateOfRecoveryMessage() {
        // Set up data
        String ID = EmailTemplates.CERTIFICATE_OF_RECOVERY;

        String EXPECTED_MESSAGE = "Thank you for your recovery documentation submission. Based on your work location, you may be required to take additional action after a period of time. A People team representative will be in touch if additional action is needed."
                + "\n\n"
                +"Please continue to follow your specific country safety guidance (including vaccination and testing frequency requirements) detailed on The Source[1]. Please be aware that testing submission frequency and timelines vary by country, and must be received no later than the deadlines described on your country page on The Source to engage in any in-person activity with Red Hatters, customers, or vendors."
                +"\n\n"
                +"If you have any questions or need assistance, please submit a ticket to the People Helpdesk[2]"
                +"\n\n"
                +"[1] https://source.redhat.com/groups/public/cim/covid_19_updates"
                +"\n\n"
                +"[2] https://redhat.service-now.com/help?id=sc_cat_item&sys_id=89e35ece1b4074102d12c880604bcb6a";
                

        // Set up mocks

        // Run test
        Message message = EmailTemplates.get(ID);

        // Validate
        assertNotNull(message);

        assertEquals(
                ID,
                message.getId(),
                "Incorrect message ID on the retrieved EmailTemplate");

        assertEquals(
                "Recovery Documentation Received",
                message.getSubject(),
                "Incorrect subject on the retrieved EmailTemplate");

        assertFalse(message.hasParans(), "Message should not have params");

        assertEquals(
                EXPECTED_MESSAGE,
                message.getText(),
                "Incorrect text on the retrieved EmailTemplate");
    }
    

    @Test
    public void test_get_success_partialVaccineMessage() {
        // Set up data
        String ID = EmailTemplates.VACCINE_PARTIAL;

        String EXPECTED_MESSAGE = "Thank you for submitting your vaccination information and if applicable, supporting documentation. This email is to confirm that we are working to review and confirm the information you submitted."
                + "\n\n"
                + "In the meantime, answers to a number of frequently asked questions (FAQ) are available on the ServiceNow COVID-19 knowledge base[1]. If you have a question not answered by these resources, please submit a ticket to the People team[2]."
                + "\n\n"
                + "[1] https://redhat.service-now.com/help?id=kb_search&spa=1&workflow_state=published&kb_knowledge_base=9f106876db5b8890ab8fe8ca4896199c"
                + "\n\n"
                + "[2] https://redhat.service-now.com/help?id=sc_cat_item&sys_id=89e35ece1b4074102d12c880604bcb6a";

        // Set up mocks

        // Run test
        Message message = EmailTemplates.get(ID);

        // Validate
        assertNotNull(message);

        assertEquals(
                ID,
                message.getId(),
                "Incorrect message ID on the retrieved EmailTemplate");

        assertEquals(
                "Vaccination Documentation Received",
                message.getSubject(),
                "Incorrect subject on the retrieved EmailTemplate");

        assertFalse(message.hasParans(), "Message should not have params");

        assertEquals(
                EXPECTED_MESSAGE,
                message.getText(),
                "Incorrect text on the retrieved EmailTemplate");
    }

    @Test
    public void test_get_success_underReviewMessage() {
        // Set up data
        String ID = EmailTemplates.VACCINE_UNDER_REVIEW;

        String EXPECTED_MESSAGE = "Thank you for submitting your vaccination information and if applicable, supporting documentation. This email is to confirm that we are working to review and confirm the information you submitted."
                + "\n\n"
                + "Your experience from this point depends on your compliance with the vaccination standard in the country where you are based. In some countries where Red Hat operates, individuals who have received one dose of a one-dose vaccination regime or two doses of a two-dose vaccination regimen are considered fully vaccinated. In others, receiving an additional booster shot is required to be considered fully vaccinated. When you receive the missing dose(s), you are expected to submit this information using the validation check-in system."
                + "\n\n"
                + "In all cases, continue to follow Red Hat’s COVID safety requirements and country-specific requirements detailed on The Source[1]. If the nature of your work does not permit you to abide by these requirements, please immediately contact the People Helpdesk[2]. You should also begin conversations with your manager about the situation. Any change in these requirements will be communicated to you directly by the People team."
                + "\n\n"
                + "Additional information is available on the ServiceNow Knowledge Base on COVID-19 frequently asked questions[3] (FAQ). Additional questions can be raised through the People Helpdesk[2]."
                + "\n\n"
                + "[1] https://source.redhat.com/groups/public/cim/covid_19_updates"
                + "\n\n"
                + "[2] https://redhat.service-now.com/help?id=sc_cat_item&sys_id=89e35ece1b4074102d12c880604bcb6a"
                + "\n\n"
                + "[3] https://redhat.service-now.com/help?id=kb_search&spa=1&workflow_state=published&kb_knowledge_base=9f106876db5b8890ab8fe8ca4896199c";

        // Set up mocks

        // Run test
        Message message = EmailTemplates.get(ID);

        // Validate
        assertNotNull(message);

        assertEquals(
                ID,
                message.getId(),
                "Incorrect message ID on the retrieved EmailTemplate");

        assertEquals(
                "Vaccination Submission Under Review",
                message.getSubject(),
                "Incorrect subject on the retrieved EmailTemplate");

        assertFalse(message.hasParans(), "Message should not have params");

        assertEquals(
                EXPECTED_MESSAGE,
                message.getText(),
                "Incorrect text on the retrieved EmailTemplate");
    }

    @Test
    public void test_get_success_acceptedMessage() {
        // Set up data
        String ID = EmailTemplates.VACCINE_ACCEPTED;

        String EXPECTED_MESSAGE = "Thank you for your submission. Your vaccination information has been reviewed and accepted. You are in compliance with Red Hat’s vaccination requirement in the country where you are based. To engage in in-person activities, follow Red Hat’s COVID safety requirements and country-specific requirements detailed on The Source[1]."
                + "\n\n"
                + "You are not required to submit any additional information through the validation check-in system at this time."
                + "\n\n"
                + "If you have any questions or need assistance, please submit a ticket to the People Helpdesk[2]."
                + "\n\n"
                + "[1] https://source.redhat.com/groups/public/cim/covid_19_updates"
                + "\n\n"
                + "[2] https://redhat.service-now.com/help?id=sc_cat_item&sys_id=89e35ece1b4074102d12c880604bcb6a";

        // Set up mocks

        // Run test
        Message message = EmailTemplates.get(ID);

        // Validate
        assertNotNull(message);

        assertEquals(
                ID,
                message.getId(),
                "Incorrect message ID on the retrieved EmailTemplate");

        assertEquals(
                "Vaccination Documentation Accepted",
                message.getSubject(),
                "Incorrect subject on the retrieved EmailTemplate");

        assertFalse(message.hasParans(), "Message should not have params");

        assertEquals(
                EXPECTED_MESSAGE,
                message.getText(),
                "Incorrect text on the retrieved EmailTemplate");
    }

    @Test
    public void test_get_success_declinedMessage() {
        // Set up data
        String ID = EmailTemplates.VACCINE_DECLINED;

        String EXPECTED_MESSAGE = "Thank you for your submission. Please be advised that a component of your submission was not accepted. Your submission was declined because the dog ate your homework. You should take immediate action to rectify this issue by reviewing the requirements below and resubmitting your vaccination information."
                + "\n\n"
                + "This article provides more information on acceptable proof and format of vaccination documentation [1]."
                + "\n\n"
                + "This article provides details on what information should be included in your proof of vaccination[2]."
                + "\n\n"
                + "We are here to work with you to ensure that your valid information and supporting documentation are accepted by the system."
                + "\n\n"
                + "Until you receive confirmation that your vaccination submission has been approved, you should not come into in-person contact with any Red Hatters, customers, or vendors. In all cases, continue to follow Red Hat’s COVID safety requirements and country-specific requirements detailed on The Source[3]. Immediately contact the People Helpdesk if the nature of your work does not permit you to abide by these requirements[4]. Any change in these requirements will be communicated to you directly by the People team."
                + "\n\n"
                + "If you have any questions or need assistance, please submit a ticket to the People Helpdesk[3]."
                + "\n\n"
                + "[1] https://redhat.service-now.com/help?id=kb_article_view&sysparm_article=KB0015355"
                + "\n\n"
                + "[2] https://redhat.service-now.com/help?id=kb_article_view&sysparm_article=KB0015848"
                + "\n\n"
                + "[3] https://source.redhat.com/groups/public/cim/covid_19_updates"
                + "\n\n"
                + "[4] https://redhat.service-now.com/help?id=sc_cat_item&sys_id=89e35ece1b4074102d12c880604bcb6a";

        // Set up mocks

        // Run test
        Message message = EmailTemplates.get(ID);

        // Validate
        assertNotNull(message);

        assertEquals(
                ID,
                message.getId(),
                "Incorrect message ID on the retrieved EmailTemplate");

        assertEquals(
                "Vaccination Documentation Declined",
                message.getSubject(),
                "Incorrect subject on the retrieved EmailTemplate");

        assertTrue(message.hasParans(), "Message should have params");

        assertEquals(
                EXPECTED_MESSAGE,
                message.getText("the dog ate your homework"),
                "Incorrect text on the retrieved EmailTemplate");
    }

    @Test
    public void test_get_success_keyDoesNotExist() {
        // Set up data

        // Set up mocks

        // Run test
        Message message = EmailTemplates.get("does not exist");

        // Validate
        assertNull(message);
    }
}
