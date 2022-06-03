package com.redhat.service;

import java.util.Set;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class KeycloakAuthUtil {

    private static final Logger LOG = LoggerFactory.getLogger(KeycloakAuthUtil.class);

    @Value("${com.redhat.agency.role.prefix}")
    private String agencyRolePrefix;

    @Value("${com.redhat.agency.role.global}")
    private String globalApproverRole;

    @Value("${com.redhat.api.role.name:api-user}")
    private String apiRole;

    public Set<String> getRealmRoles(Authentication authentication) {
        AccessToken token = getAccessToken(authentication);
        return getRealmRoles(token);
    }

    public Set<String> getRealmRoles(AccessToken token) {
        AccessToken.Access access = token.getRealmAccess();
        Set<String> roles = access.getRoles();
        LOG.debug(">>> Realm Roles: {}", roles);
        return roles;
    }

    public AccessToken getAccessToken(Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("User in unauthenticated");
        }

        return ((KeycloakSecurityContext) ((KeycloakAuthenticationToken) authentication).getCredentials()).getToken();
    }

    public boolean isSuperUser(Authentication authentication){
        Set<String> roles = getRealmRoles(authentication);

        if (LOG.isDebugEnabled()) {
            for (String role : roles) {
                LOG.debug("ROLE: " + role);
            }
        }

        return (roles!= null) && (roles.contains(globalApproverRole));
    }
    
    private String getAgencyName(Set<String> roles) {
        for (String role: roles) {
            if (role.startsWith(agencyRolePrefix)) {
                return role.substring(agencyRolePrefix.length());
            }
        }

        return null;
    }

    public String getAgencyName(Authentication authentication) {
        Set<String> roles = getRealmRoles(authentication);
        String agency = getAgencyName(roles);
        LOG.debug(">>> Realm roles from token: {}", roles);
        LOG.debug(">>> Agency name extracted from token: {}.  Role prefix: {}", agency, agencyRolePrefix);
        return agency;
    }

    public boolean hasApiRole(Authentication authentication) {
        Set<String> roles = getRealmRoles(authentication);
        return roles.contains(apiRole);
    }

}
