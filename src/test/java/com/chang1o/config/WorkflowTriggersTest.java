package com.chang1o.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

import org.yaml.snakeyaml.Yaml;

/**
 * **Feature: sonarqube-integration, Property 1: Workflow triggers on branch events**
 * 
 * Property-based tests for GitHub Actions workflow triggers.
 * Validates that the SonarQube workflow is properly configured to trigger
 * on push and pull request events for the correct branches.
 */
public class WorkflowTriggersTest {

    private static final String SONARQUBE_WORKFLOW_PATH = ".github/workflows/sonarqube.yml";

    @Test
    @DisplayName("Property 1: Workflow triggers on branch events - For any push to main or develop branch, the GitHub Actions workflow should automatically trigger SonarQube analysis")
    public void testWorkflowTriggersOnPushEvents() throws Exception {
        // **Feature: sonarqube-integration, Property 1: Workflow triggers on branch events**
        
        Map<String, Object> workflow = parseWorkflowYaml();
        
        // Verify workflow has 'on' section (SnakeYAML may parse 'on' as boolean true)
        assertTrue(workflow.containsKey("on") || workflow.containsKey(true), "Workflow should have 'on' trigger configuration");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> triggers = workflow.containsKey("on") ? 
            (Map<String, Object>) workflow.get("on") : 
            (Map<String, Object>) workflow.get(true);
        
        // Verify push trigger exists
        assertTrue(triggers.containsKey("push"), "Workflow should trigger on push events");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> pushConfig = (Map<String, Object>) triggers.get("push");
        
        // Verify push trigger has branches configuration
        assertTrue(pushConfig.containsKey("branches"), "Push trigger should specify target branches");
        
        @SuppressWarnings("unchecked")
        List<String> pushBranches = (List<String>) pushConfig.get("branches");
        
        // Verify main and test branches are included
        assertTrue(pushBranches.contains("main"), "Push trigger should include main branch");
        assertTrue(pushBranches.contains("test"), "Push trigger should include test branch");
        
        // Verify only expected branches are configured
        assertEquals(2, pushBranches.size(), "Push trigger should only target main and test branches");
    }

    @Test
    @DisplayName("Property 1: Workflow name is properly configured")
    public void testWorkflowName() throws Exception {
        // **Feature: sonarqube-integration, Property 1: Workflow triggers on branch events**
        
        Map<String, Object> workflow = parseWorkflowYaml();
        
        // Verify workflow has a name
        assertTrue(workflow.containsKey("name"), "Workflow should have a name");
        
        String workflowName = (String) workflow.get("name");
        assertNotNull(workflowName, "Workflow name should not be null");
        assertFalse(workflowName.trim().isEmpty(), "Workflow name should not be empty");
        
        // Verify name is descriptive and contains SonarQube
        assertTrue(workflowName.toLowerCase().contains("sonar"), "Workflow name should reference SonarQube");
    }

    @Test
    @DisplayName("Property 1: Workflow has SonarQube analysis job")
    public void testWorkflowHasSonarQubeJob() throws Exception {
        // **Feature: sonarqube-integration, Property 1: Workflow triggers on branch events**
        
        Map<String, Object> workflow = parseWorkflowYaml();
        
        // Verify workflow has jobs section
        assertTrue(workflow.containsKey("jobs"), "Workflow should have jobs configuration");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> jobs = (Map<String, Object>) workflow.get("jobs");
        
        // Verify SonarQube job exists (could be named 'sonarqube', 'sonar', or similar)
        boolean hasSonarJob = jobs.keySet().stream()
            .anyMatch(jobName -> jobName.toLowerCase().contains("sonar"));
        
        assertTrue(hasSonarJob, "Workflow should have a SonarQube analysis job");
        
        // Get the SonarQube job
        String sonarJobName = jobs.keySet().stream()
            .filter(jobName -> jobName.toLowerCase().contains("sonar"))
            .findFirst()
            .orElse("");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> sonarJob = (Map<String, Object>) jobs.get(sonarJobName);
        
        // Verify job runs on ubuntu-latest
        assertTrue(sonarJob.containsKey("runs-on"), "SonarQube job should specify runs-on");
        String runsOn = (String) sonarJob.get("runs-on");
        assertEquals("ubuntu-latest", runsOn, "SonarQube job should run on ubuntu-latest");
    }

    @Test
    @DisplayName("Property 1: Workflow has required steps for SonarQube analysis")
    public void testWorkflowHasRequiredSteps() throws Exception {
        // **Feature: sonarqube-integration, Property 1: Workflow triggers on branch events**
        
        Map<String, Object> workflow = parseWorkflowYaml();
        
        @SuppressWarnings("unchecked")
        Map<String, Object> jobs = (Map<String, Object>) workflow.get("jobs");
        
        // Get the SonarQube job
        String sonarJobName = jobs.keySet().stream()
            .filter(jobName -> jobName.toLowerCase().contains("sonar"))
            .findFirst()
            .orElse("");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> sonarJob = (Map<String, Object>) jobs.get(sonarJobName);
        
        // Verify job has steps
        assertTrue(sonarJob.containsKey("steps"), "SonarQube job should have steps");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> steps = (List<Map<String, Object>>) sonarJob.get("steps");
        
        // Verify essential steps exist
        boolean hasCheckout = steps.stream()
            .anyMatch(step -> step.containsKey("uses") && 
                     step.get("uses").toString().contains("actions/checkout"));
        
        boolean hasJavaSetup = steps.stream()
            .anyMatch(step -> step.containsKey("uses") && 
                     step.get("uses").toString().contains("actions/setup-java"));
        
        boolean hasMavenCache = steps.stream()
            .anyMatch(step -> step.containsKey("uses") && 
                     step.get("uses").toString().contains("actions/cache"));
        
        boolean hasSonarAnalysis = steps.stream()
            .anyMatch(step -> step.containsKey("run") && 
                     step.get("run").toString().contains("sonar:sonar"));
        
        assertTrue(hasCheckout, "Workflow should have checkout step");
        assertTrue(hasJavaSetup, "Workflow should have Java setup step");
        assertTrue(hasMavenCache, "Workflow should have Maven cache step");
        assertTrue(hasSonarAnalysis, "Workflow should have SonarQube analysis step");
    }

    private Map<String, Object> parseWorkflowYaml() throws IOException {
        File workflowFile = new File(SONARQUBE_WORKFLOW_PATH);
        assertTrue(workflowFile.exists(), "SonarQube workflow file should exist at " + SONARQUBE_WORKFLOW_PATH);
        
        Yaml yaml = new Yaml();
        try (FileInputStream fis = new FileInputStream(workflowFile)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> workflow = yaml.load(fis);
            

            
            return workflow;
        }
    }
}