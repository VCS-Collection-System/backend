package com.redhat.service;

import com.redhat.vcs.model.Employee;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CustomQueryService queryService;

    @Autowired
    KeycloakAuthUtil authUtil;

    @PostMapping("employee")
    public ResponseEntity<String> saveEmployee(Authentication authentication, @RequestBody Employee employee) {

        if (!authUtil.hasApiRole(authentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (employee == null) {
            LOG.debug(">>> Employee is null");
            return ResponseEntity.badRequest().build();
        }

        if (employee.getEmployeeId() == null || employee.getEmployeeId().trim().isEmpty()) {
            LOG.debug(">>> Employee id is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Employee id is missing");
        }

        queryService.persistOrOverwriteEmployee(employee);
        return ResponseEntity.ok().build();
    }

    @PostMapping("employee/sync")
    public ResponseEntity<String> syncEmployee(Authentication authentication, @RequestBody Employee employee) {
        if (!authUtil.hasApiRole(authentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (employee == null) {
            LOG.debug(">>> Employee is null");
            return ResponseEntity.badRequest().build();
        }

        if (employee.getEmployeeId() == null || employee.getEmployeeId().trim().isEmpty()) {
            LOG.debug(">>> Employee id is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Employee id is missing");
        }

        employeeService.sync(employee);

        return ResponseEntity.ok().build();
    }
}
