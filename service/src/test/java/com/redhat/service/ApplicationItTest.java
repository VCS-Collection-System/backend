package com.redhat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.apache.catalina.Context;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application_it.properties")
public class ApplicationItTest {
    
    @Mock
    private Context context;
    
    @Autowired
    private Application app;

    @Test
    public void webServerFactoryTest() {
        TomcatServletWebServerFactory factory = (TomcatServletWebServerFactory)app.webServerFactory();

        Collection<TomcatContextCustomizer> customizers = factory.getTomcatContextCustomizers() ;
        assertTrue(customizers.size() > 1, "Expected at least one context customizer");

        // The first customizer should be the one we added
        TomcatContextCustomizer customizer = customizers.iterator().next();

        NamingResourcesImpl resources = new NamingResourcesImpl();
        when(context.getNamingResources()).thenReturn(resources);
        customizer.customize(context);

        ContextResource cr = resources.findResource("jdbc/rhvcs-ds");
        assertNotNull(cr);
        assertEquals("org.h2.Driver", cr.getProperty("driverClassName"));
        assertEquals("jdbc:h2:./target/spring-boot-jbpm", cr.getProperty("url"));
        assertEquals("sa", cr.getProperty("username"));
        assertEquals("sa", cr.getProperty("password"));
        assertEquals("15", cr.getProperty("maxIdle"));
        assertEquals("25", cr.getProperty("maxTotal"));
        
    }
}
