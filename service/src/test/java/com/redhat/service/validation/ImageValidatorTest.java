package com.redhat.service.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

/**
 * This class contains the unit (logic / flow) tests for ImageValidator.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 */
@ExtendWith(MockitoExtension.class)
public class ImageValidatorTest {

    @InjectMocks
    private ImageValidator objectUnderTest_imageValidator;

    /**
     * This "validateUploadedImage(MultipartFile)" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - The mock MultipartFile mockFile is created inline
     *     - mockFile.getOriginalFilename returns a valid filename
     *     - mockFile.getInputStream returns the contents of a valid image (stored under the test resources)
     *
     * Calling the method under test should return a List<String> which:
     *     - Is empty
     *
     * @throws IOException This is a required throws statement, due to setting up a mock with a checked Exception
     */
    @Test
    public void test_validateUploadedImage_success() throws IOException {
        // Set up data
        // Real file data is needed due to the use of Apache Tika (even if the rest of the MultipartFile is mocked out)
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream("images/perl_problems.png");
        String filename = "valid-image.png";

        // Set up mocks
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.getOriginalFilename())
                .thenReturn(filename);
        when(mockFile.getInputStream())
                .thenReturn(imageStream);

        // Run test
        String errorMessage = objectUnderTest_imageValidator.validateUploadedImage(mockFile);

        // Validate
        assertNull(errorMessage);
    }

    /**
     * This "validateUploadedImage(MultipartFile)" test is a negative path validation, for the following condition(s):
     *     - Base filename is invalid
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - The mock MultipartFile mockFile is created inline
     *     - mockFile.getOriginalFilename returns a filename with an invalid base name, but a valid extension
     *     - mockFile.getInputStream returns the contents of a valid image (stored under the test resources)
     *
     * Calling the method under test should return a List<String> which:
     *     - Contains a single entry, indicating that the base filename is invalid
     *
     * @throws IOException This is a required throws statement, due to setting up a mock with a checked Exception
     */
    @Test
    public void test_validateUploadedImage_fail_badBaseFilename() throws IOException {
        // Set up data
        // Real file data is needed due to the use of Apache Tika (even if the rest of the MultipartFile is mocked out)
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream("images/perl_problems.png");
        String filename = "definitely%invalid.sh.png";

        // Set up mocks
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename())
                .thenReturn(filename);
        when(mockFile.getInputStream())
                .thenReturn(imageStream);

        // Run test
        String errorMessage = objectUnderTest_imageValidator.validateUploadedImage(mockFile);

        // Validate
        assertNotNull(
                errorMessage,
                "Message should be non-null on error conditions");

        assertEquals(
                "Validation errors ...\n"
                        + "Base filename does not conform to allowed pattern: [a-zA-Z0-9][a-zA-Z0-9_\\-]{0,199}",
                errorMessage,
                "Error message should indicate that the base filename is invalid");
    }

    /**
     * This "validateUploadedImage(MultipartFile)" test is a negative path validation, for the following condition(s):
     *     - File extension is invalid
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - The mock MultipartFile mockFile is created inline
     *     - mockFile.getOriginalFilename returns a filename with an invalid extension, but a valid base name
     *     - mockFile.getInputStream returns the contents of a valid image (stored under the test resources)
     *
     * Calling the method under test should return a List<String> which:
     *     - Contains a single entry, indicating that the file extension is invalid
     *
     * @throws IOException This is a required throws statement, due to setting up a mock with a checked Exception
     */
    @Test
    public void test_validateUploadedImage_fail_badFileExtension() throws IOException {
        // Set up data
        // Real file data is needed due to the use of Apache Tika (even if the rest of the MultipartFile is mocked out)
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream("images/perl_problems.png");
        String badFileExtension = "brrrrrrrrrrrrr";
        String filename = "it-was-almost-valid." + badFileExtension;

        // Set up mocks
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.getOriginalFilename())
                .thenReturn(filename);
        when(mockFile.getInputStream())
                .thenReturn(imageStream);

        // Run test
        String errorMessage = objectUnderTest_imageValidator.validateUploadedImage(mockFile);

        // Validate
        assertNotNull(
                errorMessage,
                "Message should be non-null on error conditions");

        assertEquals(
                "Validation errors ...\n"
                        + "File extension is not allowed: " + badFileExtension,
                errorMessage,
                "Error message should indicate that the file extension is invalid");
    }

    /**
     * This "validateUploadedImage(MultipartFile)" test is a negative path validation, for the following condition(s):
     *     - Mime type is incorrect
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - The mock MultipartFile mockFile is created inline
     *     - mockFile.getOriginalFilename returns a valid filename
     *     - mockFile.getInputStream returns the contents of an invalid image (stored under the test resources)
     *
     * Calling the method under test should return a List<String> which:
     *     - Contains a single entry, indicating that mime type is invalid
     *
     * @throws IOException This is a required throws statement, due to setting up a mock with a checked Exception
     */
    @Test
    public void test_validateUploadedImage_fail_badMimeType() throws IOException {
        // Set up data
        // Real file data is needed due to the use of Apache Tika (even if the rest of the MultipartFile is mocked out)
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream("images/totally-not-fake.jpg");
        String filename = "definitely-not-a-script.jpg";

        // Set up mocks
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.getOriginalFilename())
                .thenReturn(filename);
        when(mockFile.getInputStream())
                .thenReturn(imageStream);

        // Run test
        String errorMessage = objectUnderTest_imageValidator.validateUploadedImage(mockFile);

        // Validate
        assertNotNull(
                errorMessage,
                "Message should be non-null on error conditions");

        assertEquals(
                "Validation errors ...\n"
                        + "Invalid mime type detected: application/x-sh",
                errorMessage,
                "Error message should indicate that the mime type is invalid");
    }

    /**
     * This "validateUploadedImage(MultipartFile)" test is a negative path validation, for the following condition(s):
     *     - Base filename is invalid
     *     - File extension is invalid
     *     - Mime type is incorrect
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - The mock MultipartFile mockFile is created inline
     *     - mockFile.getOriginalFilename returns a filename with both an invalid base name and extension
     *     - mockFile.getInputStream returns the contents of an invalid image (stored under the test resources)
     *
     * Calling the method under test should return a List<String> which:
     *     - Contains three entries, indicating:
     *         - Invalid base filename
     *         - Invalid file extension
     *         - Invalid mime type
     *
     * @throws IOException This is a required throws statement, due to setting up a mock with a checked Exception
     */
    @Test
    public void test_validateUploadedImage_fail_multipleErrors() throws IOException {
        // Set up data
        // Real file data is needed due to the use of Apache Tika (even if the rest of the MultipartFile is mocked out)
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream("images/totally-not-fake.jpg");
        String badFileExtension = "brrrrrrrrrrrrr";
        String filename = "_hahaha-script-masquerading-as-image-go." + badFileExtension;

        // Set up mocks
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.getOriginalFilename())
                .thenReturn(filename);
        when(mockFile.getInputStream())
                .thenReturn(imageStream);

        // Run test
        String errorMessage = objectUnderTest_imageValidator.validateUploadedImage(mockFile);

        // Validate
        assertNotNull(
                errorMessage,
                "Message should be non-null on error conditions");

        assertEquals(
                "Validation errors ...\n"
                        + "Base filename does not conform to allowed pattern: [a-zA-Z0-9][a-zA-Z0-9_\\-]{0,199}\n"
                        + "File extension is not allowed: " + badFileExtension + "\n"
                        + "Invalid mime type detected: application/x-sh",
                errorMessage,
                "Error message should contain multiple lines with three error conditions");
    }
}
