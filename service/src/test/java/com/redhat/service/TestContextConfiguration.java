package com.redhat.service;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestContextConfiguration {

    @Bean
    public S3Service s3Service() {
        return new S3Service("localhost",8080,"us-east-1");
    }
}
