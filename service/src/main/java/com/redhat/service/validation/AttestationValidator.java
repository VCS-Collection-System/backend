package com.redhat.service.validation;

import java.util.List;
import java.util.stream.Collectors;

import com.redhat.service.KeycloakAuthUtil;
import com.redhat.vcs.model.Document;
import com.redhat.vcs.model.Employee;

import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * This class contains methods for validating data in the AttestationController.
 *
 */
@Component
public class AttestationValidator {

    @Autowired
    private KeycloakAuthUtil authUtil;

    /**
     * Check if the employee first and last name match the one in sso token
     *
     * @param authentication Authentication piece storing access token
     * @param employee Employee record
     * @param document Document record
     */
    public String validateToken(Authentication authentication, Employee employee, Document document) {
        String errorMessage = null;

        if (document.getSubmittedBy() == null || document.getSubmittedBy().trim().isEmpty()) {
            AccessToken accessToken = authUtil.getAccessToken(authentication);

            String firstName = accessToken.getGivenName();
            String lastName  = accessToken.getFamilyName();

            if (firstName != null && !firstName.equals(employee.getFirstName())) {
                errorMessage = "The employee first name in the payload '" + employee.getFirstName()
                        + "' does not match the one retrieved from sso token '" + firstName + "'";
            } else if (lastName != null && !lastName.equals(employee.getLastName())) {
                errorMessage = "The employee last name in the payload '" + employee.getLastName()
                        + "' does not match the one retrieved from sso token '" + lastName + "'";
            }
        }

        return errorMessage;
    }

    /**
     * Use the `validate` method built into the document to determine if there are any validation errors; if so,
     * aggregate them into a single error message and throw an exception.
     *
     * @param document the document to validate
     * @throws IllegalArgumentException if document.validate() produces any errors
     */
    public String validateDocument(Document document) {
        String errorMessage = null;
        List<String> validationErrors = document.validate();

        if (!validationErrors.isEmpty()) {
            errorMessage = validationErrors.stream()
                    .collect(Collectors.joining("\n", "Validation errors ...\n", ""));
        }

        return errorMessage;
    }

    /**
     * Use the `validate` method built into the employee to determine if there are any validation errors; if so,
     * aggregate them into a single error message and throw an exception.
     *
     * @param employee the employee to validate
     */
    public String validateEmployee(Employee employee) {
        String errorMessage = null;
        List<String> validationErrors = employee.validate();

        if (!validationErrors.isEmpty()) {
            errorMessage = validationErrors.stream()
                    .collect(Collectors.joining("\n", "Validation errors ...\n", ""));
        }

        return errorMessage;
    }
}
