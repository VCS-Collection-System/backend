package com.redhat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import com.redhat.vcs.model.Employee;
import com.redhat.vcs.model.EmployeeLog;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * This class contains the unit (logic / flow) tests for EmployeeServiceTest.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 */
@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {
    @Mock
    private EntityManager em;

    @InjectMocks
    private EmployeeService objectUnderTest_employeeService;

    // Used to capture an Employee that is passed into em.merge, for inspection
    @Captor
    private ArgumentCaptor<Employee> employeeCaptor;

    /**
     * This "update(Employee)" test is a happy path validation, for the following condition(s):
     *     - Employee does not exist in database
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - EntityManager will return a null employee
     *
     * Calling the method under test should return an Employee which:
     *     - Is the same object as the input Employee
     *
     * The following interactions are verified, in addition to the return value:
     *     - em.persist is called once
     *     - em.merge is never called
     */
    @Test
    public void test_update_success_notFound() {
        // Set up data
        final String EMPLOYEE_ID = "You";
        final String EMAIL = "you@example.org";
        final String ALTERNATE_EMAIL = "me@example.org";
        final LocalDate DOB = LocalDate.of(2003, 1, 1);

        Employee employeeIn = new Employee();

        employeeIn.setEmployeeId(EMPLOYEE_ID);
        employeeIn.setEmail(EMAIL);
        employeeIn.setAlternateEmail(ALTERNATE_EMAIL);
        employeeIn.setDateOfBirth(DOB);

        Employee employeeDb = null;

        // Set up mocks
        TypedQuery<Employee> mockBaseQuery = mock(TypedQuery.class);
        TypedQuery<Employee> mockQueryWithEmpIdParam = mock(TypedQuery.class);

        when(em.createQuery("FROM Employee WHERE employeeId = :employeeId", Employee.class))
                .thenReturn(mockBaseQuery);
        when(mockBaseQuery.setParameter("employeeId", EMPLOYEE_ID))
                .thenReturn(mockQueryWithEmpIdParam);
        when(mockQueryWithEmpIdParam.getSingleResult())
                .thenReturn(employeeDb);

        // Run test
        Employee employeeOut = objectUnderTest_employeeService.update(employeeIn);

        // Validate
        // When not found in DB, input and output employee should be the SAME objects:
        assertSame(
                employeeIn,
                employeeOut,
                "Input and output Employee should NOT be the same object");

        verify(em, times(1))
                .persist(employeeIn);

        verify(em, never())
                .merge(any(Employee.class));
    }

    /**
     * This "update(Employee)" test is a happy path validation, for the following condition(s):
     *     - Employee already exists in database
     *     - Passed in employee and database employee are equivalent
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - EntityManager will return an Employee which is equivalent to the input employee
     *
     * Calling the method under test should return an Employee which:
     *     - Has the same fields as the input Employee
     *     - Is not the same object as the input Employee
     *
     * The following interactions are verified, in addition to the return value:
     *     - em.persist is never called
     *     - em.merge is never called
     */
    @Test
    public void test_update_success_foundNotTouched() {
        // Set up data
        final String EMPLOYEE_ID = "You";
        final String EMAIL = "you@example.org";
        final String ALTERNATE_EMAIL = "me@example.org";
        final LocalDate DOB = LocalDate.of(2003, 1, 1);

        Employee employeeIn = new Employee();

        employeeIn.setEmployeeId(EMPLOYEE_ID);
        employeeIn.setEmail(EMAIL);
        employeeIn.setAlternateEmail(ALTERNATE_EMAIL);
        employeeIn.setDateOfBirth(DOB);

        // Same info as "in", but with no DOB
        Employee employeeDb = new Employee();
        employeeDb.setEmployeeId(EMPLOYEE_ID);
        employeeDb.setEmail(EMAIL);
        employeeDb.setAlternateEmail(ALTERNATE_EMAIL);
        employeeDb.setDateOfBirth(DOB);

        // Set up mocks
        TypedQuery<Employee> mockBaseQuery = mock(TypedQuery.class);
        TypedQuery<Employee> mockQueryWithEmpIdParam = mock(TypedQuery.class);

        when(em.createQuery("FROM Employee WHERE employeeId = :employeeId", Employee.class))
                .thenReturn(mockBaseQuery);
        when(mockBaseQuery.setParameter("employeeId", EMPLOYEE_ID))
                .thenReturn(mockQueryWithEmpIdParam);
        when(mockQueryWithEmpIdParam.getSingleResult())
                .thenReturn(employeeDb);

        // Run test
        Employee employeeOut = objectUnderTest_employeeService.update(employeeIn);

        // Validate
        // Input and output employee should be DIFFERENT objects, but with the same values:
        assertNotSame(
                employeeIn,
                employeeOut,
                "Input and output Employee should NOT be the same object");

        assertEquals(
                employeeIn,
                employeeOut,
                "Input and output Employee should be equals");

        verify(em, never())
                .persist(any(Employee.class));

        verify(em, never())
                .merge(any(Employee.class));
    }

    /**
     * This "update(Employee)" test is a happy path validation, for the following condition(s):
     *     - Employee already exists in database
     *     - Database employee is missing DOB field
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - EntityManager will return an Employee, with DOB not set
     *
     * Calling the method under test should return an Employee which:
     *     - Has the same fields as the input Employee
     *     - Is not the same object as the input Employee
     *
     * The following interactions are verified, in addition to the return value:
     *     - em.persist is never called
     *     - em.merge is called once
     */
    @Test
    public void test_update_success_foundAndTouched_dob() {
        // Set up data
        final String EMPLOYEE_ID = "You";
        final String EMAIL = "you@example.org";
        final String ALTERNATE_EMAIL = "me@example.org";
        final LocalDate DOB = LocalDate.of(2003, 1, 1);

        Employee employeeIn = new Employee();

        employeeIn.setEmployeeId(EMPLOYEE_ID);
        employeeIn.setEmail(EMAIL);
        employeeIn.setAlternateEmail(ALTERNATE_EMAIL);
        employeeIn.setDateOfBirth(DOB);

        // Same info as "in", but with no DOB
        Employee employeeDb = new Employee();
        employeeDb.setEmployeeId(EMPLOYEE_ID);
        employeeDb.setEmail(EMAIL);
        employeeDb.setAlternateEmail(ALTERNATE_EMAIL);

        // Set up mocks
        TypedQuery<Employee> mockBaseQuery = mock(TypedQuery.class);
        TypedQuery<Employee> mockQueryWithEmpIdParam = mock(TypedQuery.class);

        when(em.createQuery("FROM Employee WHERE employeeId = :employeeId", Employee.class))
                .thenReturn(mockBaseQuery);
        when(mockBaseQuery.setParameter("employeeId", EMPLOYEE_ID))
                .thenReturn(mockQueryWithEmpIdParam);
        when(mockQueryWithEmpIdParam.getSingleResult())
                .thenReturn(employeeDb);

        // Run test
        objectUnderTest_employeeService.update(employeeIn);

        // Capture the Employee passed into em.merge:
        verify(em).merge(employeeCaptor.capture());
        Employee mergedEmployee = employeeCaptor.getValue();

        // Validate
        // Input and output employee should be DIFFERENT objects, but with the same values:
        assertNotSame(
                employeeIn,
                mergedEmployee,
                "Input and merged Employee should NOT be the same object");

        assertEquals(
                employeeIn,
                mergedEmployee,
                "Input and merged Employee should be equals");

        verify(em, never())
                .persist(any(Employee.class));
    }

    /**
     * This "update(Employee)" test is a happy path validation, for the following condition(s):
     *     - Employee already exists in database
     *     - Database employee is missing alternate email field
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - EntityManager will return an Employee, with DOB not set
     *
     * NOTE: Since the EntityManger is mocked, and em.merge overrides the output value, this test cannot verify the
     * return value (`output`) in the traditional way; instead, this test will capture the value passed into
     * em.merge and verify that it matches what we are expecting.
     *
     * The following interactions are verified, in addition to the return value:
     *     - em.persist is never called
     *     - em.merge is called once
     */
    @Test
    public void test_update_success_foundAndTouched_altEmail() {
        // Set up data
        final String EMPLOYEE_ID = "You";
        final String EMAIL = "you@example.org";
        final String ALTERNATE_EMAIL = "me@example.org";
        final LocalDate DOB = LocalDate.of(2003, 1, 1);

        Employee employeeIn = new Employee();

        employeeIn.setEmployeeId(EMPLOYEE_ID);
        employeeIn.setEmail(EMAIL);
        employeeIn.setAlternateEmail(ALTERNATE_EMAIL);
        employeeIn.setDateOfBirth(DOB);

        // Same info as "in", but with no DOB
        Employee employeeDb = new Employee();
        employeeDb.setEmployeeId(EMPLOYEE_ID);
        employeeDb.setEmail(EMAIL);
        employeeDb.setAlternateEmail(null);

        // Set up mocks
        TypedQuery<Employee> mockBaseQuery = mock(TypedQuery.class);
        TypedQuery<Employee> mockQueryWithEmpIdParam = mock(TypedQuery.class);

        when(em.createQuery("FROM Employee WHERE employeeId = :employeeId", Employee.class))
                .thenReturn(mockBaseQuery);
        when(mockBaseQuery.setParameter("employeeId", EMPLOYEE_ID))
                .thenReturn(mockQueryWithEmpIdParam);
        when(mockQueryWithEmpIdParam.getSingleResult())
                .thenReturn(employeeDb);

        // Run test
        objectUnderTest_employeeService.update(employeeIn);

        // Capture the Employee passed into em.merge:
        verify(em).merge(employeeCaptor.capture());
        Employee mergedEmployee = employeeCaptor.getValue();

        // Validate
        // Input and output employee should be DIFFERENT objects, but with the same values:
        assertNotSame(
                employeeIn,
                mergedEmployee,
                "Input and merged Employee should NOT be the same object");

        assertEquals(
                employeeIn,
                mergedEmployee,
                "Input and merged Employee should be equals");

        verify(em, never())
                .persist(any(Employee.class));
    }

    /**
     * This "update(Employee)" test is a happy path validation, for the following condition(s):
     *     - Employee already exists in database
     *     - Passed-in employee is missing DOB field
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - EntityManager will return an Employee, with DOB not set
     *
     * Calling the method under test should return an Employee which:
     *     - Has the same fields as the input Employee
     *     - Is not the same object as the input Employee
     *
     * The following interactions are verified, in addition to the return value:
     *     - em.persist is never called
     *     - em.merge is never called
     */
    @Test
    public void test_update_success_foundNotTouchedNullDOB() {
        // Set up data
        final String EMPLOYEE_ID = "You";
        final String EMAIL = "you@example.org";
        final String ALTERNATE_EMAIL = "me@example.org";
        final LocalDate DOB = LocalDate.of(2003, 1, 1);

        Employee employeeIn = new Employee();

        employeeIn.setEmployeeId(EMPLOYEE_ID);
        employeeIn.setEmail(EMAIL);
        employeeIn.setAlternateEmail(ALTERNATE_EMAIL);

        // Same info as "in", but with no DOB
        Employee employeeDb = new Employee();
        employeeDb.setEmployeeId(EMPLOYEE_ID);
        employeeDb.setEmail(EMAIL);
        employeeDb.setAlternateEmail(ALTERNATE_EMAIL);
        employeeDb.setDateOfBirth(DOB);

        // Set up mocks
        TypedQuery<Employee> mockBaseQuery = mock(TypedQuery.class);
        TypedQuery<Employee> mockQueryWithEmpIdParam = mock(TypedQuery.class);

        when(em.createQuery("FROM Employee WHERE employeeId = :employeeId", Employee.class))
                .thenReturn(mockBaseQuery);
        when(mockBaseQuery.setParameter("employeeId", EMPLOYEE_ID))
                .thenReturn(mockQueryWithEmpIdParam);
        when(mockQueryWithEmpIdParam.getSingleResult())
                .thenReturn(employeeDb);

        // Run test
        Employee employeeOut = objectUnderTest_employeeService.update(employeeIn);

        // Validate
        // Input and output employee should be DIFFERENT objects, but with the same values:
        assertNotSame(
                employeeIn,
                employeeOut,
                "Input and output Employee should NOT be the same object");

        assertNotEquals(
                employeeIn.getDateOfBirth(),
                employeeOut.getDateOfBirth(),
                "Input and output Employee should have a different date of birth");

        verify(em, never())
                .persist(any(Employee.class));

        verify(em, never())
                .merge(employeeOut);
    }

    /**
     * This "update(Employee)" test is a negative path validation, for the following condition(s):
     *     - Alternate e-mail is invalid
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - EntityManager will return a null employee
     *
     * Calling the method under test should throw the following Exception:
     *     - RuntimeException
     */
    @Test
    public void test_update_fail_badAltEmail() {
        // Set up data
        final String EMPLOYEE_ID = "You";
        final String EMAIL = "you@example.org";
        final String ALTERNATE_EMAIL = "AAAAAHHHHHHHHHHHH!!!";
        final LocalDate DOB = LocalDate.of(2003, 1, 1);

        Employee employeeIn = new Employee();

        employeeIn.setEmployeeId(EMPLOYEE_ID);
        employeeIn.setEmail(EMAIL);
        employeeIn.setAlternateEmail(ALTERNATE_EMAIL);
        employeeIn.setDateOfBirth(DOB);

        // Same info as "in", but with no DOB
        Employee employeeDb = null;

        // Set up mocks
        TypedQuery<Employee> mockBaseQuery = mock(TypedQuery.class);
        TypedQuery<Employee> mockQueryWithEmpIdParam = mock(TypedQuery.class);

        when(em.createQuery("FROM Employee WHERE employeeId = :employeeId", Employee.class))
                .thenReturn(mockBaseQuery);
        when(mockBaseQuery.setParameter("employeeId", EMPLOYEE_ID))
                .thenReturn(mockQueryWithEmpIdParam);
        when(mockQueryWithEmpIdParam.getSingleResult())
                .thenReturn(employeeDb);

        // Run test & Validate (one step due to thrown exception)
        assertThrows(RuntimeException.class, () -> objectUnderTest_employeeService.update(employeeIn));
    }

    /**
     * This "sync(Employee)" test is a negative path validation, for the following condition(s):
     *     - Employee is null
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - em.find returns null
     *
     * The method under test has no return value.
     *
     * The following interactions are verified, in lieu of a return value:
     *     - em.persist should be called once
     *     - em.merge should never be called
     */
    @Test
    public void test_sync_success_persist() {
        // Set up data
        final Long ID = 1L;
        final String EMPLOYEE_ID = "One Ell";

        Employee ldapEmployee = new Employee();
        ldapEmployee.setId(ID);
        ldapEmployee.setEmployeeId(EMPLOYEE_ID);

        // Set up mocks
        when(em.find(Employee.class, ID))
                .thenReturn(null);

        // Run test
        objectUnderTest_employeeService.sync(ldapEmployee);

        // Validate
        verify(em, times(1))
                .persist(ldapEmployee);

        verify(em, never())
                .merge(any(Employee.class));
    }

    /**
     * This "sync(Employee)" test is a happy path validation, for the following condition(s):
     *     - Employee is found in db, but no LDAP fields have been changed
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - em.find returns a test Employee
     *
     * The method under test has no return value.
     *
     * The following interactions are verified, in lieu of a return value:
     *     - em.persist should never be called
     *     - em.merge should be called once
     */
    @Test
    public void test_sync_success_merge_noLdapFieldChange() {
        // Set up data
        final Long ID = 1L;
        final String EMPLOYEE_ID = "One Ell";
        final Boolean IS_HR = Boolean.FALSE;

        Employee ldapEmployee = new Employee();
        ldapEmployee.setId(ID);
        ldapEmployee.setEmployeeId(EMPLOYEE_ID);
        ldapEmployee.setHR(IS_HR);

        Employee dbEmployee = new Employee();
        dbEmployee.setId(ID);
        dbEmployee.setEmployeeId(EMPLOYEE_ID);
        dbEmployee.setHR(IS_HR);

        // Set up mocks
        when(em.find(Employee.class, ID))
                .thenReturn(dbEmployee);

        // Run test
        objectUnderTest_employeeService.sync(ldapEmployee);

        // Validate
        verify(em, never())
                .persist(any(Employee.class));

        verify(em, times(1))
                .merge(dbEmployee);
    }

    /**
     * This "sync(Employee)" test is a happy path validation, for the following condition(s):
     *     - Employee is found in db, an an LDAP field has been changed
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - em.find returns a test Employee
     *
     * The method under test has no return value.
     *
     * The following interactions are verified, in lieu of a return value:
     *     - em.persist should be called once (for the EmployeeLog)
     *     - em.merge should be called once
     */
    @Test
    public void test_sync_success_merge_ldapFieldChange() {
        // Set up data
        final Long ID = 1L;
        final String EMPLOYEE_ID = "One Ell";
        final Boolean IS_HR = Boolean.FALSE;
        final Boolean NEW_IS_HR = Boolean.TRUE;

        Employee ldapEmployee = new Employee();
        ldapEmployee.setId(ID);
        ldapEmployee.setEmployeeId(EMPLOYEE_ID);
        ldapEmployee.setHR(NEW_IS_HR);

        Employee dbEmployee = new Employee();
        dbEmployee.setId(ID);
        dbEmployee.setEmployeeId(EMPLOYEE_ID);
        dbEmployee.setHR(IS_HR);

        // Set up mocks
        when(em.find(Employee.class, ID))
                .thenReturn(dbEmployee);

        // Run test
        objectUnderTest_employeeService.sync(ldapEmployee);

        // Validate
        verify(em, times(1))
                .persist(any(EmployeeLog.class));

        verify(em, times(1))
                .merge(dbEmployee);
    }

    /**
     * This "sync(Employee)" test is a negative path validation, for the following condition(s):
     *     - Employee is null
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should throw the following Exception:
     *     - RuntimeException
     */
    @Test
    public void test_sync_fail_nullEmployee() {
        // Set up data
        Employee ldapEmployee = null;

        // Set up mocks

        // Run test & Validate (one step due to thrown exception)
        assertThrows(RuntimeException.class, () -> objectUnderTest_employeeService.sync(ldapEmployee));
    }

    /**
     * This "sync(Employee)" test is a negative path validation, for the following condition(s):
     *     - Employee Id is null
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should throw the following Exception:
     *     - RuntimeException
     */
    @Test
    public void test_sync_fail_nullEmployeeId() {
        // Set up data
        final Long ID = 1L;
        final String EMPLOYEE_ID = null;

        Employee ldapEmployee = new Employee();
        ldapEmployee.setId(ID);
        ldapEmployee.setEmployeeId(EMPLOYEE_ID);

        // Set up mocks

        // Run test & Validate (one step due to thrown exception)
        assertThrows(RuntimeException.class, () -> objectUnderTest_employeeService.sync(ldapEmployee));
    }

    /**
     * This "sync(Employee)" test is a negative path validation, for the following condition(s):
     *     - Employee Id is empty
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should throw the following Exception:
     *     - RuntimeException
     */
    @Test
    public void test_sync_fail_emptyEmployeeId() {
        // Set up data
        final Long ID = 1L;
        final String EMPLOYEE_ID = " ";

        Employee ldapEmployee = new Employee();
        ldapEmployee.setId(ID);
        ldapEmployee.setEmployeeId(EMPLOYEE_ID);

        // Set up mocks

        // Run test & Validate (one step due to thrown exception)
        assertThrows(RuntimeException.class, () -> objectUnderTest_employeeService.sync(ldapEmployee));
    }

    /**
     * This "sync(Employee)" test is a negative path validation, for the following condition(s):
     *     - Exception due to Employee not found in database
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - EntityManager will throw a NoResultException
     *
     * Calling the method under test should throw the following Exception:
     *     - NoResultException (i.e., the one from the mock behavior)
     */
    @Test
    public void test_sync_fail_noResultException() {
        // Set up data
        final Long ID = 1L;
        final String EMPLOYEE_ID = "Won";

        Employee ldapEmployee = new Employee();
        ldapEmployee.setId(ID);
        ldapEmployee.setEmployeeId(EMPLOYEE_ID);

        // Set up mocks
        when(em.find(Employee.class, ID))
                .thenThrow(new NoResultException("This exception must be thrown for this test to be passed"));

        // Run test & Validate (one step due to thrown exception)
        assertThrows(NoResultException.class, () -> objectUnderTest_employeeService.sync(ldapEmployee));
    }
}
