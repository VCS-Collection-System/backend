package com.redhat.service;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

/**
 * This class provides a set of web services wrapping calls to kjar services. This decouples
 * client code from underlying backend implementation details, such as which kjar container
 * to call. This class is able to determine which container should be called, as well as
 * resolving JSON differences (i.e.., class names) between legacy and current versions of the
 *  application.
 */
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private static final Logger LOG = LoggerFactory.getLogger(TaskController.class);

    @Value("${kieserver.location}")
    private String kieserverBase;

    @Autowired
    private KeycloakRestTemplate restTemplate;

    /**
     * Pass-through call to the kie server for retrieving task information. This method will
     * look up the container id so that the caller does not need to know the underlying 
     * container id.
     * 
     * @param request Incoming servlet request
     * @param taskId Task ID to retrieve information for
     * @return JSON task information
     */
    @GetMapping(value = "{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getTaskInfo(
            HttpServletRequest request,
            @PathVariable Long taskId) {

        String jsonTaskInformation = retrieveTaskInformation(taskId, request.getQueryString());
        
        ResponseEntity<String> response;

        if (jsonTaskInformation == null) {
            response = ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    "No task information found for task ID " + taskId);
        } else {
            response = ResponseEntity.ok(jsonTaskInformation);
        }

        return response;
    }

    /**
     * Pass-through call to the kie server for retreiving potential owner information. The 
     * query string contained in the request will be passed along.
     * 
     * @param request  Incoming servlet request
     * @return Potential owners for the task based on input paramaters.
     */
    @GetMapping(path = "owners", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getOwners(
            HttpServletRequest request) {
        return ResponseEntity.ok(executeGetRequest(kieserverBase + "/queries/tasks/instances/pot-owners?" + 
            request.getQueryString() ));
    }

    /**
     * Pass-through call to the kie server for updating task state. The query string contained 
     * in the request will be passed along.
     * 
     * @param request Incoming servlet request
     * @param taskId Task ID to update the state of
     * @param state Task state, e.g., claimed, completed
     * @param payload Body to be sent long with the request
     * @return 200 on success, 500 on error 
     */
    @PutMapping(path = "{taskId}/states/{state}",
                consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setTaskState(
            HttpServletRequest request,
            @PathVariable Long taskId,
            @PathVariable String state,
            @RequestBody String payload) {
        String containerId = retrieveContainerId(taskId);
        String uri = kieserverBase + "/containers/" + containerId + "/tasks/" + taskId + "/states/" + state;

        if(request.getQueryString() != null) {
            uri += '?' + request.getQueryString();
        }

        ResponseEntity<String> response;

        if (executePutRequest(uri, payload)) {
            response = ResponseEntity.ok().build();
        } else {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                body("Failed to set state for task " + taskId);
        }
        
        return response;
    }

    /**
     * Pass-through call to the kie server for signaling a process. This is called by auto-approver
     * to pass the eagle eye results.
     *
     * @param request Incoming servlet request
     * @param taskId Task ID to update the state of
     * @param state Task state, e.g., claimed, completed
     * @param payload Body to be sent long with the request
     * @return 200 on success, 500 on error
     */
    @PostMapping(path = "containers/{containerId}/processes/instances/{processInstanceId}/signal/{signalName}",
                consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> signalProcess(
            @PathVariable String containerId,
            @PathVariable Integer processInstanceId,
            @PathVariable String signalName,
            @RequestBody String payload) {

        String uri = kieserverBase + "/containers/" + containerId + "/processes/instances/" + processInstanceId + "/signal/" + signalName;

        ResponseEntity<String> response = executePostRequest(uri, payload);

        if ( response == null) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                body("Failed to signal process " +  processInstanceId + " for container " + containerId + " with signal " + signalName);
        }

        return response;
    }

    /**
     * Calls PAM to retrieve task information for the given task. This method will
     * look up the container id associated with the task in order to call the 
     * correct kjar service.
     * 
     * @param taskId Id of the task to retreieve information for
     * @param queryString Query parameters to pass along
     * @return Task information as JSON on success, null otherwise
     */
    private String retrieveTaskInformation(Long taskId, String queryString) {
        String taskInformation = null;
        String containerId = retrieveContainerId(taskId);
        if (containerId != null) {
           final String uri = kieserverBase + "/containers/" + containerId + 
            "/tasks/" + taskId + '?' + queryString;

            try {
                taskInformation = restTemplate.getForObject(uri, String.class);
            } catch (RestClientResponseException re) {
                LOG.error("Error retrieving task information for container {} task {}", containerId, taskId, re);
            }
        } else {
            LOG.error("Unable to retrive KIE Container ID for task {}", taskId);
        }
        
        return taskInformation;
    }

    /**
     * Given a task id, retrieves the corresponding container id.
     * 
     * @param taskId Task ID to retrieve the container of
     * @return Contaner ID on success, null otherswise
     */
    private String retrieveContainerId(Long taskId) {
        String containerId = null;
    
        final String uri = kieserverBase + "/queries/tasks/instances/" + taskId;
        String jsonTaskInfo = executeGetRequest(uri);

        if (jsonTaskInfo != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonTaskInfo);
                containerId = jsonObject.getString("task-container-id");
            } catch (JSONException je) {
                LOG.error("Caught exception parsing JSON", je);
            }
        }

        LOG.debug("Task ID {} has container ID {}", taskId, containerId);
        return containerId;
    }

    /**
     * Executes an HTTP GET request, authenticated via keycloak
     * 
     * @param uri URI to issue the GET request against
     * @return Text string (JSON) returned from the get on successs, null
     *         otherwise.
     */
    private String executeGetRequest (String uri) {
        String result = null;

        try {
            LOG.debug("Executing HTTP GET on {}", uri);
            result = restTemplate.getForObject(uri, String.class);
        } catch (RestClientResponseException re) {
            LOG.error("Error executing HTTP GET for URI {}", uri, re);
        }

        return result;
    }

    /**
     * Executes an HTTP PUT request, authenticated via keycloak
     * 
     * @param uri URI to issue the PUT request against
     * @param payload Content (i.e., JSON) to send with the PUT request
     * @return true on success, false otherwise
     */
    private boolean executePutRequest(String uri, String payload) {
        boolean success = true;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        try {
            LOG.debug("Executing HTTP PUT on {} with payload {}", uri, payload);
            restTemplate.put(uri, entity);
        } catch (RestClientException re) {
            LOG.error("Unable to execute HTTP PUT for URI {}", uri, re);
            success = false;
        }

        return success;
    }

    /**
     * Executes an HTTP POST request, authenticated via keycloak
     *
     * @param uri URI to issue the POST request against
     * @param payload Content to send with the POST request
     * @return Response from service or null on failure calling service
     */
    private ResponseEntity<String> executePostRequest(String uri, String payload) {
        ResponseEntity<String> result = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        try {
            LOG.debug("Executing HTTP POST on {} with payload {}", uri, payload);
            result = restTemplate.postForEntity(uri, entity, String.class);
        } catch (RestClientException re) {
            LOG.error("Unable to execute HTTP POST for URI {}", uri, re);
        }

        return result;
    }

}
