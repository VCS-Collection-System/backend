package com.redhat.service;

import javax.sql.DataSource;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.keycloak.adapters.springsecurity.client.KeycloakClientRequestFactory;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.MapPropertySource;

@SpringBootApplication
public class Application {

    @Value("${s3.bucket.host:#{null}}")
    private String bucketHost;

    @Value("${s3.bucket.port:#{null}}")
    private Integer bucketPort;

    @Value("${s3.bucket.region}")
    private String bucketRegion;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${kjar.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.dbcp2.initial-size}")
    private String minConnections;
    
    @Value("${spring.datasource.dbcp2.max-total}")
    private String maxConnections;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.addListeners(new SystemPropertiesSetterEventListener());
        springApplication.run(args);
    }

    @Bean
    public S3Service s3Service() {
        return new S3Service(bucketHost, bucketPort, bucketRegion);
    }

    @Bean
    public KeycloakRestTemplate keycloakRestTemplate() {
        return new KeycloakRestTemplate(new KeycloakClientRequestFactory());
    }

    private static class SystemPropertiesSetterEventListener
            implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
        @Override
        public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
            event.getEnvironment().getPropertySources().forEach(ps -> {
                if (ps instanceof MapPropertySource) {
                    MapPropertySource mps = (MapPropertySource) ps;

                    mps.getSource().forEach((key, value) -> {
                        if ("vcs.environment".equals(key)
                                || "vcs.email.enable".equals(key)) {
                            System.getProperties().putIfAbsent(key, value.toString());
                        }
                    });
                }
            });
        }
    }

    /**
     * 
     * Explicitly create an embedded tomcat instance so that JNDI can be enabled and
     * a JTA datasource can be created within tomcat. This datasource is used in the
     * kjars so they can create entity manager factories within their own classloaders.
     * This allows each kjar to have access to their own entities without conflicting
     * with any other entities in the application (i.e., entities in other deployed
     * kjars or within the service implementation itself.)
     * 
     * @return Instance of the embedded tomcat server
     */
    @Bean
    public ServletWebServerFactory webServerFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory(){
            @Override
            protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
                tomcat.enableNaming();
                return new TomcatWebServer(tomcat, this.getPort() >= 0, this.getShutdown());
            }
        };
        factory.addContextCustomizers(new TomcatContextCustomizer() {
            @Override
            public void customize(Context context) {
				ContextResource resource = new ContextResource();
				resource.setName("jdbc/rhvcs-ds");
				resource.setType(DataSource.class.getName());
                resource.setProperty("driverClassName", driverClassName);
                resource.setProperty("url", url);
                resource.setProperty("username", username);
                resource.setProperty("password", password);
                resource.setProperty("maxIdle", minConnections);
                resource.setProperty("maxTotal", maxConnections);
				context.getNamingResources().addResource(resource);
            }
            });
            return factory;
        }

}
