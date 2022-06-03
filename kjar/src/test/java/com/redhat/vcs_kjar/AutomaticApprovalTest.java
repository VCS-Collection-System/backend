package com.redhat.vcs_kjar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Objects;

import com.redhat.vcs.model.Employee;
import com.redhat.vcs.model.ProofType;
import com.redhat.vcs.model.VaccineCardDocument;
import com.redhat.vcs.model.VaccineDocumentList;

import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * This class contains the unit (logic / flow) tests for AutomaticApproval.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 *
 * Note: Due to the (unenforced) contract between equals and hashCode, both methods will be tested together.
 */
public class AutomaticApprovalTest {
    /**
     * This "toString()" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a String which:
     *     - Contains all of the data from the AutomaticApproval
     */
    @Test
    public void test_toString_success() {
        // Set up data
        final boolean IS_APPLICABLE = true;

        AutomaticApproval approval = new AutomaticApproval();
        approval.setApplicable(IS_APPLICABLE);

        final String EXPECTED_THRESHOLD_STRING = "AutomaticApproval ["
                + "isApplicable=" + IS_APPLICABLE
                + "]";

        // Set up mocks

        // Run test
        String approvalString = approval.toString();

        // Validate
        assertEquals(
                EXPECTED_THRESHOLD_STRING,
                approvalString,
                "AutomaticApproval String does not match the expected format");
    }

    /**
     * This "equals(Object)" test is a happy path validation, for the following condition(s):
     *     - Objects are equal
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a boolean value which:
     *     - Is "true"
     */
    @Test
    public void test_equals_hashcode_success_equivalentObjects() {
        // Set up data
        final boolean IS_APPLICABLE = true;

        AutomaticApproval approval = new AutomaticApproval();

        approval.setApplicable(IS_APPLICABLE);

        AutomaticApproval other = new AutomaticApproval();

        other.setApplicable(IS_APPLICABLE);

        // Set up mocks

        // Run test
        boolean equals = approval.equals(other);

        // Validate
        assertTrue(
                equals,
                "AutomaticApproval constructed from the same values should be equal");

        assertEquals(
                approval.hashCode(),
                other.hashCode(),
                "When two objects are equal, their hash codes should also be equal");
    }

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
    public void test_equals_hashcode_success_equal_sameObject() {
        // Set up data
        AutomaticApproval approval = new AutomaticApproval();
        AutomaticApproval other = approval;

        // Set up mocks

        // Run test
        boolean equals = approval.equals(other);

        // Validate
        assertTrue(
                equals,
                "An object should always be equal to itself");

        assertEquals(
                approval.hashCode(),
                other.hashCode(),
                "If an object remains the same, hashCode should continually evaluate to the same value");
    }

    /**
     * This "equals(Object)" test is a happy path validation, for the following condition(s):
     *     - Other object is null
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a boolean value which:
     *     - Is "false"
     */
    @Test
    public void test_equals_hashcode_success_notEqual_null() {
        // Set up data
        AutomaticApproval approval = new AutomaticApproval();
        AutomaticApproval other = null;

        // Set up mocks

        // Run test
        boolean equals = approval.equals(other);

        // Validate
        assertFalse(
                equals,
                "The equals method should never return true for a null value");

        assertNotEquals(
                approval.hashCode(),
                // NOTE: Using Objects.hash, since `other` (null) cannot be dereferenced
                Objects.hash(other),
                "When two objects are not equal, their hash codes should also be not equal");
    }

    /**
     * This "equals(Object)" test is a happy path validation, for the following condition(s):
     *     - Other object is not a AutomaticApproval
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a boolean value which:
     *     - Is "false"
     */
    @Test
    public void test_equals_hashcode_success_notEqual_differentClass() {
        // Set up data
        AutomaticApproval approval = new AutomaticApproval();
        Long other = 42L;

        // Set up mocks

        // Run test
        boolean equals = approval.equals(other);

        // Validate
        assertFalse(
                equals,
                "Objects of different classes should never be equal");

        assertNotEquals(
                approval.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");
    }

    /**
     * This "equals(Object)" test is a happy path validation, for the following condition(s):
     *     - Objects are the same class, but contain different values
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a boolean value which:
     *     - Is "false"
     */
    @Test
    public void test_equals_hashcode_success_notEqual_differentValues() {
        // Set up data
        final boolean IS_APPLICABLE = false;

        AutomaticApproval approval = new AutomaticApproval();
        AutomaticApproval other = new AutomaticApproval();

        approval.setApplicable(IS_APPLICABLE);
        other.setApplicable(!IS_APPLICABLE);

        // Set up mocks

        // Run test & Validate - Multiple cases below:
        boolean equals = approval.equals(other);

        // Validate
        assertFalse(
                equals,
                "AutomaticApproval should not be equal unless all fields are equal between then");

        assertNotEquals(
                approval.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");
    }

    @Test
    public void testAutoApproverApplicability() {
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieSession kSession = kContainer.newKieSession();

		VaccineCardDocument vcd = new VaccineCardDocument();
                Employee e = new Employee();
                vcd.setEmployee(e);
                //TESTING FOR INDIA
		vcd.setProofType(ProofType.DIVOC);
                ArrayList<VaccineCardDocument> vaccineCardDocuments = new ArrayList<VaccineCardDocument>(){{
                        add(vcd); 
                    }};
                VaccineDocumentList vaccineDocumentList = new VaccineDocumentList();
                vaccineDocumentList.setDocuments(vaccineCardDocuments);

		AutomaticApproval autoApproval = new AutomaticApproval();
		kSession.insert(vaccineDocumentList.getDocuments());
		kSession.insert(autoApproval);
		kSession.getAgenda().getAgendaGroup("vax-card-automatic-approval-applicable").setFocus();
		kSession.fireAllRules();

                assertTrue(autoApproval.isApplicable());
	}

}
