package com.redhat.vcs.model;

import java.security.SecureRandom;
import java.util.regex.Pattern;

public class Utils {
    private Utils() {
        // Empty constructor to prevent instantiation
    }


    // The email regex below comes from OWASP: https://owasp.org/www-community/OWASP_Validation_Regex_Repository
    // NOTE: SonarQube raises the bug "Refactor this repetition that can lead to a stack overflow for large inputs."
    //       This bug is mitigated by checking the length of the email input string before executing the
    //       regex. This is done in the isEmailValid method.
    //       
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"); // NOSONAR
        
    // The Following constants are given default scope to support
    // unit testing
    static final int DEFAULT_MIN_LENGTH = 5;
    static final int DEFAULT_MAX_LENGTH = 20;

    // Maximum email length https://www.rfc-editor.org/rfc/rfc3696#page-5
    static final int MAX_EMAIL_LENGTH = 320;
    static final char DEFAULT_MASK_CHARACTER = '*';
    static final String MASKED_DOB_STRING = "**/**/****";

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isEmailValid(String s) {
        return s != null && s.length() <= MAX_EMAIL_LENGTH && EMAIL_PATTERN.matcher(s).matches();
    }

    public static boolean isEmailBlankOrValid(String s) {
        return isBlank(s) || isEmailValid(s);
    }

    public static String maskedDOB() {
        return MASKED_DOB_STRING;
    }

    public static String maskedString() {
        return maskedString(DEFAULT_MIN_LENGTH, DEFAULT_MAX_LENGTH, DEFAULT_MASK_CHARACTER);
    }

    public static String maskedString(int minLength, int maxLength) {
        return maskedString(minLength, maxLength, DEFAULT_MASK_CHARACTER);
    }

    public static String maskedString(int minLength, int maxLength, char character) {
        SecureRandom secureRandom = new SecureRandom();
        int numChars = (minLength == maxLength) ? minLength : secureRandom.nextInt(maxLength - minLength ) + minLength;
        return new String(new char[numChars]).replace('\0',character);
    }

}
