package com.chang1o.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

/**
 * **Feature: sonarqube-integration, Property 2: Workflow triggers on PR events**
 * 
 * Property-based tests for GitHub Actions workflow Pull Request triggers.
 * Validates that the SonarQube workflow is properly configured to trigger
 * on Pull Request events for the correct branches and event types.
 */
public class PRTriggersTest {

    private static final String SONARQUBE_WORKFLOW_PATH = ".github/workflows/sonarqube.yml";

    @Test
    @DisplayName("Property 2: Workflow triggers on PR events - For any Pull Request creation or update targeting main or test, the GitHub Actions workflow should automatically trigger SonarQube analysis")
    public void testWorkflowTriggersOnPullRequestEvents() throws Exception {
        // **Feature: sonarqube-integration, Property 2: Workflow triggers on PR events**
        
        Map<String, Object> workflow = parseWorkflowYaml();
        
        // Get triggers configuration (handle SnakeYAML parsing 'on' as boolean true)
        @SuppressWarnings("unchecked")
        Map<String, Object> triggers = workflow.containsKey("on") ? 
            (Map<String, Object>) workflow.get("on") : 
            (Map<String, Object>) workflow.get(true);
        
        // Verify pull_request trigger exists
        assertTrue(triggers.containsKey("pull_request"), "Workflow should trigger on pull_request events");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> prConfig = (Map<String, Object>) triggers.get("pull_request");
        
        // Verify pull_request trigger has branches configuration
        assertTrue(prConfig.containsKey("branches"), "Pull request trigger should specify target branches");
        
        @SuppressWarnings("unchecked")
        List<String> prBranches = (List<String>) prConfig.get("branches");
        
        // Verify main and test branches are included
        assertTrue(prBranches.contains("main"), "Pull request trigger should include main branch");
        assertTrue(prBranches.contains("test"), "Pull request trigger should include test branch");
        
        // Verify only expected branches are configured
        assertEquals(2, prBranches.size(), "Pull request trigger should only target main and test branches");
    }

    @Test
    @DisplayName("Property 2: Pull Request trigger has correct event types")
    public void testPullRequestEventTypes() throws Exception {
        // **Feature: sonarqube-integration, Property 2: Workflow triggers on PR events**
        
        Map<String, Object> workflow = parseWorkflowYaml();
        
        @SuppressWarnings("unchecked")
        Map<String, Object> triggers = workflow.containsKey("on") ? 
            (Map<String, Object>) workflow.get("on") : 
            (Map<String, Object>) workflow.get(true);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> prConfig = (Map<String, Object>) triggers.get("pull_request");
        
        // Verify pull_request trigger has types configuration
        assertTrue(prConfig.containsKey("types"), "Pull request trigger should specify event types");
        
        @SuppressWarnings("unchecked")
        List<String> prTypes = (List<String>) prConfig.get("types");
        
        // Verify essential PR event types are included
        assertTrue(prTypes.contains("opened"), "Pull request trigger should include 'opened' event type");
        assertTrue(prTypes.contains("synchronize"), "Pull request trigger should include 'synchronize' event type");
        assertTrue(prTypes.contains("reopened"), "Pull request trigger should include 'reopened' event type");
        
        // Verify we have the expected number of event types
        assertEquals(3, prTypes.size(), "Pull request trigger should have exactly 3 event types");
    }

    @Test
    @DisplayName("Property 2: Workflow triggers on both push and pull request events")
    public void testWorkflowHasBothTriggerTypes() throws Exception {
        // **Feature: sonarqube-integration, Property 2: Workflow triggers on PR events**
        
        Map<String, Object> workflow = parseWorkflowYaml();
        
        @SuppressWarnings("unchecked")
        Map<String, Object> triggers = workflow.containsKey("on") ? 
            (Map<String, Object>) workflow.get("on") : 
            (Map<String, Object>) workflow.get(true);
        
        // Verify both push and pull_request triggers exist
        assertTrue(triggers.containsKey("push"), "Workflow should have push trigger");
        assertTrue(triggers.containsKey("pull_request"), "Workflow should have pull_request trigger");
        
        // Verify triggers target the same branches
        @SuppressWarnings("unchecked")
        Map<String, Object> pushConfig = (Map<String, Object>) triggers.get("push");
        @SuppressWarnings("unchecked")
        Map<String, Object> prConfig = (Map<String, Object>) triggers.get("pull_request");
        
        @SuppressWarnings("unchecked")
        List<String> pushBranches = (List<String>) pushConfig.get("branches");
        @SuppressWarnings("unchecked")
        List<String> prBranches = (List<String>) prConfig.get("branches");
        
        // Verify both triggers target the same branches
        assertEquals(pushBranches.size(), prBranches.size(), "Push and PR triggers should target the same number of branches");
        assertTrue(pushBranches.containsAll(prBranches), "Push and PR triggers should target the same branches");
        assertTrue(prBranches.containsAll(pushBranches), "Push and PR triggers should target the same branches");
    }

    @Test
    @DisplayName("Property 2: Workflow configuration supports SonarQube PR decoration")
    public void testWorkflowSupportsPRDecoration() throws Exception {
        // **Feature: sonarqube-integration, Property 2: Workflow triggers on PR events**
        
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
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> steps = (List<Map<String, Object>>) sonarJob.get("steps");
        
        // Verify checkout step has fetch-depth: 0 for better analysis
        boolean hasDeepCheckout = steps.stream()
            .anyMatch(step -> {
                if (step.containsKey("uses") && step.get("uses").toString().contains("actions/checkout")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> with = (Map<String, Object>) step.get("with");
                    return with != null && "0".equals(String.valueOf(with.get("fetch-depth")));
                }
                return false;
            });
        
        assertTrue(hasDeepCheckout, "Checkout step should have fetch-depth: 0 for better SonarQube analysis");
        
        // Verify SonarQube analysis step has GITHUB_TOKEN for PR decoration
        boolean hasGitHubToken = steps.stream()
            .anyMatch(step -> {
                if (step.containsKey("run") && step.get("run").toString().contains("sonar:sonar")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> env = (Map<String, Object>) step.get("env");
                    return env != null && env.containsKey("GITHUB_TOKEN");
                }
                return false;
            });
        
        assertTrue(hasGitHubToken, "SonarQube analysis step should have GITHUB_TOKEN for PR decoration");
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