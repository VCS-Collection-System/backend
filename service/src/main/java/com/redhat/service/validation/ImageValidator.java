package com.redhat.service.validation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * This class contains methods for validating an uploaded image.
 */
@Component
public class ImageValidator {
    /**
     * Base filenames (i.e., text before the extension) must adhere to the following pattern:
     *     - Starts with an alphanumeric character
     *     - Is no longer than 200 characters
     *     - Consists of only alphanumeric characters, dashes, and underscores.
     */
    private static final String VAX_IMAGE_BASE_FILENAME_REGEX
            = "[a-zA-Z0-9]"
            + "[a-zA-Z0-9_\\-]{0,199}";

    /**
     * Image files must be one of the following formats: JPEG, PNG
     */
    private static final String[] ALLOWED_FILE_EXTENSIONS = {"jpg", "jpeg", "png"};

    /**
     * Image files must have a content type associate with one of the allowed file formats, per ALLOWED_FILE_EXTENSIONS
     */
    private static final String[] ALLOWED_MIME_TYPES = {"image/jpeg", "image/png"};

    private static final Logger LOG = LoggerFactory.getLogger(ImageValidator.class);

    /**
     * This method validates that an uploaded image file matches the accepted pattern for filenames, the filename has
     * an extension on the allowlist, and the content type matches the file extension.
     *
     * @param file the uploaded file to validate
     * @return the string listing all errors during image file validation (or null, on successful validation)
     */
    public String validateUploadedImage(MultipartFile file) {
        String errorMessage = null;
        List<String> validationErrors = new ArrayList<>();

        validateBaseFilename(file.getOriginalFilename(), validationErrors);
        validateFileExtension(file.getOriginalFilename(), validationErrors);
        validateContentType(file, validationErrors);

        if (!validationErrors.isEmpty()) {
            errorMessage = validationErrors.stream()
                    .collect(Collectors.joining("\n", "Validation errors ...\n", ""));
        }

        return errorMessage;
    }

    /**
     * This method validates that the base filename is acceptable per the provided regex. Files with multiple extensions
     * (e.g., filename.sh.png) will fail validation.
     *
     * @param filename the filename to verify
     * @param validationErrors the aggregate list of validation errors
     */
    private void validateBaseFilename(String filename, List<String> validationErrors) {
        if (!FilenameUtils.getBaseName(filename).matches(VAX_IMAGE_BASE_FILENAME_REGEX)) {
            validationErrors
                    .add("Base filename does not conform to allowed pattern: " + VAX_IMAGE_BASE_FILENAME_REGEX);
        }
    }

    /**
     * This method validates that the extension on the filename is on the allowlist. If validation fails, an error is
     * added to the validationErrors list.
     *
     * @param filename the filename to verify the extension of
     * @param validationErrors the aggregate list of validation errors
     */
    private void validateFileExtension(String filename, List<String> validationErrors) {
        String givenFileExtension = FilenameUtils.getExtension(filename);
        boolean valid = false;

        for (String allowedExtension : ALLOWED_FILE_EXTENSIONS) {
            if (allowedExtension.equalsIgnoreCase(givenFileExtension)) {
                valid = true;
                break;
            }
        }

        if (!valid) {
            validationErrors.add("File extension is not allowed: " + givenFileExtension);
        }
    }

    /**
     * This method validates that the uploaded file has the correct content type, to prevent malicious files from being
     * uploaded under an innocuous filename (e.g., a script named `super-safe-image.png`).
     *
     * @param file the file to check the mime type validity of
     * @param validationErrors the aggregate list of validation errors
     */
    private void validateContentType(MultipartFile file, List<String> validationErrors) {
        try {
            boolean valid = false;
            String detectedMimeType = detectMimeType(file);

            for (String allowedMimeType : ALLOWED_MIME_TYPES) {
                if (allowedMimeType.equalsIgnoreCase(detectedMimeType)) {
                    valid = true;
                    break;
                }
            }

            if (!valid) {
                validationErrors.add("Invalid mime type detected: " + detectedMimeType);
            }
        } catch (TikaException | IOException e) {
            LOG.debug("Error determining mime type: {}", e.getLocalizedMessage(), e);

            validationErrors.add("Failed to determine Mime Type for file");
        }
    }

    /**
     * Detects the mime type of a given file.
     *
     * @param file the file to determine the mime type for.
     * @return the mime type of the file
     * @throws TikaException if there is a failure in the Tika API
     * @throws IOException if there is a failure in the Tika API, or the InputStream is invalid
     */
    private String detectMimeType(MultipartFile file) throws TikaException, IOException {
        String filename = file.getOriginalFilename();

        // Initialize the TikaConfig, which will be used to perform mime type detection
        TikaConfig tika = new TikaConfig();

        // In order to provide additional information to Tika, create Metadata and add filename to it
        Metadata metadata = new Metadata();
        metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, filename);

        String detectedMimeType = tika.getDetector()
                .detect(TikaInputStream.get(file.getInputStream()), metadata)
                .toString();

        LOG.debug("For file '{}', detected the following Mime Type: {}", filename, detectedMimeType);

        return detectedMimeType;
    }
}
