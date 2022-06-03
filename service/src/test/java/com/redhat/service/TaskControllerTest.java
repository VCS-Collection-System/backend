package com.redhat.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;


@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    @Mock
    HttpServletRequest httpRequest;

    @Mock
    private KeycloakRestTemplate keyCloakRestTemplate;

    @InjectMocks
    private TaskController objectUnderTest_taskController;

    @Test
    public void getTaskInfoNominal() throws Exception {
        String queryString = "something=else&parm2=another_value";
        String responseString = "{\"container-info\":abcd}";
        String containerId = "my_task_container_id";

        when(httpRequest.getQueryString()).thenReturn(queryString);
        when(keyCloakRestTemplate.getForObject(endsWith("queries/tasks/instances/1"),any())).thenReturn("{\"task-container-id\":" + containerId +"}");
        when(keyCloakRestTemplate.getForObject(endsWith("/containers/" + containerId + "/tasks/1?" + queryString),any())).thenReturn(responseString);

        ResponseEntity<String> response = objectUnderTest_taskController.getTaskInfo(httpRequest, 1L);
        assertEquals(responseString, response.getBody());
    }


    @Test
    public void getTaskInfoException1() throws Exception {
        String queryString = "something=else&parm2=another_value";
        String responseString = "No task information found for task ID 1";
        String containerId = "my_task_container_id";

        when(httpRequest.getQueryString()).thenReturn(queryString);
        when(keyCloakRestTemplate.getForObject(endsWith("queries/tasks/instances/1"),any())).thenReturn("{\"task-container-id\":" + containerId +"}");
        when(keyCloakRestTemplate.getForObject(endsWith("/containers/" + containerId + "/tasks/1?" + queryString),any())).thenThrow(new RestClientResponseException("Exception!",500,"Error!",null,null,null));

        ResponseEntity<String> response = objectUnderTest_taskController.getTaskInfo(httpRequest, 1L);
        assertEquals(responseString, response.getBody());
    }

    @Test
    public void getTaskInfoException2() throws Exception {
        String queryString = "something=else&parm2=another_value";
        String responseString = "No task information found for task ID 1";

        when(httpRequest.getQueryString()).thenReturn(queryString);
        when(keyCloakRestTemplate.getForObject(endsWith("queries/tasks/instances/1"),any())).thenThrow(new RestClientResponseException("Exception!",500,"Error!",null,null,null));

        ResponseEntity<String> response = objectUnderTest_taskController.getTaskInfo(httpRequest, 1L);
        assertEquals(responseString, response.getBody());
    }

    @Test
    public void getTaskInfoInvalidJson() throws Exception {
        String queryString = "something=else&parm2=another_value";
        String responseString = "No task information found for task ID 1";

        when(httpRequest.getQueryString()).thenReturn(queryString);
        when(keyCloakRestTemplate.getForObject(endsWith("queries/tasks/instances/1"),any())).thenReturn("Not JSON");

        ResponseEntity<String> response = objectUnderTest_taskController.getTaskInfo(httpRequest, 1L);
        assertEquals(responseString, response.getBody());
    }

    @Test
    public void getTaskInfoNull() throws Exception {
        String queryString = "something=else&parm2=another_value";
        String responseString = null;
        String containerId = "my_task_container_id";

        when(httpRequest.getQueryString()).thenReturn(queryString);
        when(keyCloakRestTemplate.getForObject(endsWith("queries/tasks/instances/1"),any())).thenReturn("{\"task-container-id\":" + containerId +"}");
        when(keyCloakRestTemplate.getForObject(endsWith("/containers/" + containerId + "/tasks/1?" + queryString),any())).thenReturn(responseString);

        ResponseEntity<String> response = objectUnderTest_taskController.getTaskInfo(httpRequest, 1L);
        assertEquals("No task information found for task ID 1", response.getBody());
    }


    @Test
    public void getTaskInfoNoJson() throws Exception {
        String queryString = "something=else&parm2=another_value";
        String responseString = null;
        String containerId = "my_task_container_id";

        when(httpRequest.getQueryString()).thenReturn(queryString);
        when(keyCloakRestTemplate.getForObject(endsWith("queries/tasks/instances/1"),any())).thenReturn("{\"task-container-id\":" + containerId +"}");
        when(keyCloakRestTemplate.getForObject(endsWith("/containers/" + containerId + "/tasks/1?" + queryString),any())).thenReturn(responseString);

        ResponseEntity<String> response = objectUnderTest_taskController.getTaskInfo(httpRequest, 1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getOwnersNominal() throws Exception {
        String queryString = "something=else&parm2=another_value";
        String responseString = "{\"pot-owners\":\"owners\"}";

        when(httpRequest.getQueryString()).thenReturn(queryString);
        when(keyCloakRestTemplate.getForObject(endsWith("/queries/tasks/instances/pot-owners?" + queryString),any())).thenReturn(responseString);

        ResponseEntity<String> response = objectUnderTest_taskController.getOwners(httpRequest);
        assertEquals(responseString, response.getBody());
    }

    @Test
    public void setTaskStateNominal() throws Exception {
        String queryString = "something=else&parm2=another_value";
        String payload = "{\"payload\": \"value\"}";
        Long taskId = 1L;
        String state = "completed";
        String containerId = "my_task_container_id";    

        String uri = "/containers/" + containerId + "/tasks/1/states/completed";

        when(httpRequest.getQueryString()).thenReturn(queryString);
        when(keyCloakRestTemplate.getForObject(endsWith("queries/tasks/instances/1"),any())).thenReturn("{\"task-container-id\":" + containerId +"}");
        doNothing().when(keyCloakRestTemplate).put(endsWith(uri + '?' + queryString),any()); 

        ResponseEntity<String> response = objectUnderTest_taskController.setTaskState(httpRequest,taskId,state,payload);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void setTaskStateNominalNoQueryString() throws Exception {
        String payload = "{\"payload\": \"value\"}";
        Long taskId = 1L;
        String state = "completed";
        String containerId = "my_task_container_id";    
        String uri = "/containers/" + containerId + "/tasks/1/states/completed";

        when(httpRequest.getQueryString()).thenReturn(null);
        when(keyCloakRestTemplate.getForObject(endsWith("queries/tasks/instances/1"),any())).thenReturn("{\"task-container-id\":" + containerId +"}");
        doNothing().when(keyCloakRestTemplate).put(endsWith(uri),any());

        ResponseEntity<String> response = objectUnderTest_taskController.setTaskState(httpRequest,taskId,state,payload);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void setTaskStateRestClientException() throws Exception {
        String payload = "{\"payload\": \"com.redhat.vcs\"}";
        Long taskId = 1L;
        String state = "completed";
        String containerId = "my_task_container_id";
        String uri = "/containers/" + containerId + "/tasks/1/states/completed";
        String containerInfo = "{\"container-info\" : \"com.redhat.vcs\"";

        when(httpRequest.getQueryString()).thenReturn(null);
        when(keyCloakRestTemplate.getForObject(endsWith("queries/tasks/instances/1"),any())).thenReturn("{\"task-container-id\":" + containerId +"}");
        // when(keyCloakRestTemplate.getForObject(contains("/containers/" + containerId + "/tasks/1"),any())).thenReturn(containerInfo);
        
        doThrow(new RestClientException("Expected Exception")).when(keyCloakRestTemplate).put(endsWith(uri),any());

        ResponseEntity<String> response = objectUnderTest_taskController.setTaskState(httpRequest,taskId,state,payload);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void signalProcessNominal() throws Exception{
        String payload = "Sample payload";
        String containerId = "my_task_container_id";
        int processInstanceId = 1234;
        String signalName = "Signal1";
        String responseData = "Data";

        ResponseEntity<String> re = ResponseEntity.ok(responseData);
        when(keyCloakRestTemplate.postForEntity(endsWith(signalName),any(),eq(String.class))).thenReturn(re);

        ResponseEntity<String> response = objectUnderTest_taskController.signalProcess(containerId, processInstanceId, signalName, payload);

        assertEquals(response.getBody(), re.getBody());
    }


    @Test
    public void signalProcessNullResponse() throws Exception{
        String payload = "Sample payload";
        String containerId = "my_task_container_id";
        int processInstanceId = 1234;
        String signalName = "Signal1";

        when(keyCloakRestTemplate.postForEntity(endsWith(signalName),any(),eq(String.class))).thenReturn(null);

        ResponseEntity<String> response = objectUnderTest_taskController.signalProcess(containerId, processInstanceId, signalName, payload);

        assertEquals(response.getBody(), "Failed to signal process " + processInstanceId + " for container " + containerId + " with signal " + signalName);
    }

    @Test
    public void signalProcessRestClientException() throws Exception{
        String payload = "Sample payload";
        String containerId = "my_task_container_id";
        int processInstanceId = 1234;
        String signalName = "Signal1";

        when(keyCloakRestTemplate.postForEntity(endsWith(signalName),any(),eq(String.class))).thenThrow(new RestClientResponseException("Exception!",500,"Error!",null,null,null));

        ResponseEntity<String> response = objectUnderTest_taskController.signalProcess(containerId, processInstanceId, signalName, payload);

        assertEquals(response.getBody(), "Failed to signal process " + processInstanceId + " for container " + containerId + " with signal " + signalName);
    }

}
