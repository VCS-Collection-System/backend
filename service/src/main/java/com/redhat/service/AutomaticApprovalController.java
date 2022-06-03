package com.redhat.service;

import java.util.Collections;
import java.util.Map;

import com.redhat.vcs.model.VaxProofAutomaticApprovalResponse;

import org.jbpm.services.api.ProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/approve")
public class AutomaticApprovalController {

    private static final Logger LOG = LoggerFactory.getLogger(AutomaticApprovalController.class);

    @Autowired
    private ProcessService processService;

    @PostMapping("vax-proof")
    public ResponseEntity<String> submitReviewResults
        (
            Authentication authentication,
            @RequestPart(name = "reviewResults") VaxProofAutomaticApprovalResponse vaxProofAutomaticApprovalResponse
        ) {

        try {
            if (vaxProofAutomaticApprovalResponse != null
                    && vaxProofAutomaticApprovalResponse.getCorrelationId() != null) {
                // complete service task.
                Map<String, Object> params
                        = Collections.singletonMap("automaticApprovalResponse", vaxProofAutomaticApprovalResponse);
                Long taskId = Long.parseLong(vaxProofAutomaticApprovalResponse.getCorrelationId());
                processService.completeWorkItem(taskId, params);
                LOG.info("Attempted completion of automatic approval service.\\n\\ttask/correlationId:{}\\n\\t{}",
                        taskId, vaxProofAutomaticApprovalResponse);
            }
            return ResponseEntity.accepted().build();
        } catch (AuthorizationException ae) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ae.getMessage());
        } catch (Exception e) {
            LOG.error("Error while submitting review results from automatic approval service.\n\t{}",
                    vaxProofAutomaticApprovalResponse, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
