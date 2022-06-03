package com.redhat.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

public class S3Service {

    private static final Logger LOG = LoggerFactory.getLogger(S3Service.class);

    @Value("${s3.bucket.name}")
    private String bucketName;

    private final S3Client s3;

    /*
     * Credentials will be picked up using various methods.
     * See: https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials.html
     */
    public S3Service(String bucketHost, Integer bucketPort, String bucketRegion) {
        S3ClientBuilder s3Builder = S3Client.builder()
                .region(Region.of(bucketRegion));

        if (bucketHost != null && bucketPort != null) {
            URI uri = null;

            try {
                uri = new URI(
                        bucketPort == 443 ? "https" : "http",
                        null,
                        bucketHost,
                        bucketPort,
                        null,
                        null,
                        null);
            } catch (URISyntaxException e) {
                LOG.error("Failed to construct S3 URI. Falling back to default", e);
            }

            if (uri != null) {
                LOG.info("S3 URI: {}", uri);
                s3Builder.endpointOverride(uri);
            }
        }

        s3 = s3Builder.build();
    }

    /**
     * Save the file to S3 and return the object uuid
     */
    public String put(byte[] file) throws S3Exception {
        String objectKey = UUID.randomUUID().toString();

        PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        PutObjectResponse response = s3.putObject(putOb, RequestBody.fromBytes(file));
        LOG.debug("Successfully saved object to AWS S3. S3 uuid: {}, Response: {}", objectKey, response);

        return objectKey;
    }

    public byte [] get(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ResponseBytes<GetObjectResponse> o = s3.getObject(getObjectRequest, ResponseTransformer.toBytes());
        LOG.debug("Successfully retrieve object with key {} from AWS S3", key);
        return o.asByteArray();
    }

    public String delete(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
                s3.deleteObject(deleteObjectRequest);

        DeleteObjectResponse deleteObjectResponse= s3.deleteObject(deleteObjectRequest);
        LOG.debug("Successfully deleted object with key {} from AWS S3, Response: {}", key, deleteObjectResponse);
        return key;
    }
}
