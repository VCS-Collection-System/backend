package com.redhat.service;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@Ignore
@RunWith(SpringRunner.class)
@Import(TestContextConfiguration.class)
@PropertySource("classpath:application.properties")
public class S3ServiceTest {

    @Autowired
    private S3Service s3;

    @Test
    public void testPutAndGetAndDelete() {
        byte [] expected = UUID.randomUUID().toString().getBytes();

        String key = s3.put(expected);
        assertNotNull(key);

        byte [] actual = s3.get(key);
        assertArrayEquals(expected, actual);

        s3.delete(key);

        byte [] actualValue = s3.get(key);
        assertArrayEquals(null, actualValue);
       
    }
}
