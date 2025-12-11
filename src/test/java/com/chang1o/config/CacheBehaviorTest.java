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
 * **Feature: sonarqube-integration, Property 16: Cache restoration & Property 17: Cache creation**
 * 
 * Property-based tests for GitHub Actions workflow caching behavior.
 * Validates that the SonarQube workflow properly configures Maven dependency caching
 * for improved build performance.
 */
public class CacheBehaviorTest {

    private static final String SONARQUBE_WORKFLOW_PATH = ".github/workflows/sonarqube.yml";

    @Test
    @DisplayName("Property 16: Cache restoration - For any workflow start with available cache, Maven dependencies should be restored from cache")
    public void testMavenCacheRestoration() throws Exception {
        // **Feature: sonarqube-integration, Property 16: Cache restoration**
        
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
        
        // Verify Maven cache step exists
        boolean hasMavenCache = steps.stream()
            .anyMatch(step -> step.containsKey("uses") && 
                     step.get("uses").toString().contains("actions/cache"));
        
        assertTrue(hasMavenCache, "Workflow should have Maven cache step for dependency restoration");
        
        // Get the cache step
        Map<String, Object> cacheStep = steps.stream()
            .filter(step -> step.containsKey("uses") && 
                   step.get("uses").toString().contains("actions/cache"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(cacheStep, "Cache step should be found");
        
        // Verify cache step has 'with' configuration
        assertTrue(cacheStep.containsKey("with"), "Cache step should have 'with' configuration");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> cacheConfig = (Map<String, Object>) cacheStep.get("with");
        
        // Verify cache path is configured for Maven
        assertTrue(cacheConfig.containsKey("path"), "Cache step should specify path");
        String cachePath = (String) cacheConfig.get("path");
        assertEquals("~/.m2", cachePath, "Cache path should be ~/.m2 for Maven dependencies");
    }

    @Test
    @DisplayName("Property 17: Cache creation - For any successful workflow completion, Maven dependencies should be cached for future runs")
    public void testMavenCacheCreation() throws Exception {
        // **Feature: sonarqube-integration, Property 17: Cache creation**
        
        Map<String, Object> workflow = parseWorkflowYaml();
        
        @SuppressWarnings("unchecked")
        Map<String, Object> jobs = (Map<String, Object>) workflow.get("jobs");
        
        String sonarJobName = jobs.keySet().stream()
            .filter(jobName -> jobName.toLowerCase().contains("sonar"))
            .findFirst()
            .orElse("");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> sonarJob = (Map<String, Object>) jobs.get(sonarJobName);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> steps = (List<Map<String, Object>>) sonarJob.get("steps");
        
        // Get the cache step
        Map<String, Object> cacheStep = steps.stream()
            .filter(step -> step.containsKey("uses") && 
                   step.get("uses").toString().contains("actions/cache"))
            .findFirst()
            .orElse(null);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> cacheConfig = (Map<String, Object>) cacheStep.get("with");
        
        // Verify cache key is configured
        assertTrue(cacheConfig.containsKey("key"), "Cache step should specify cache key");
        String cacheKey = (String) cacheConfig.get("key");
        
        // Verify cache key includes runner OS and pom.xml hash
        assertTrue(cacheKey.contains("${{ runner.os }}"), "Cache key should include runner OS");
        assertTrue(cacheKey.contains("${{ hashFiles('**/pom.xml') }}"), "Cache key should include pom.xml hash for dependency changes");
        
        // Verify restore-keys is configured for fallback
        assertTrue(cacheConfig.containsKey("restore-keys"), "Cache step should specify restore-keys for fallback");
        String restoreKeys = (String) cacheConfig.get("restore-keys");
        assertTrue(restoreKeys.contains("${{ runner.os }}"), "Restore keys should include runner OS for fallback");
    }

    @Test
    @DisplayName("Property 16 & 17: Java setup step also enables Maven caching")
    public void testJavaSetupMavenCaching() throws Exception {
        // **Feature: sonarqube-integration, Property 16: Cache restoration & Property 17: Cache creation**
        
        Map<String, Object> workflow = parseWorkflowYaml();
        
        @SuppressWarnings("unchecked")
        Map<String, Object> jobs = (Map<String, Object>) workflow.get("jobs");
        
        String sonarJobName = jobs.keySet().stream()
            .filter(jobName -> jobName.toLowerCase().contains("sonar"))
            .findFirst()
            .orElse("");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> sonarJob = (Map<String, Object>) jobs.get(sonarJobName);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> steps = (List<Map<String, Object>>) sonarJob.get("steps");
        
        // Find Java setup step
        Map<String, Object> javaSetupStep = steps.stream()
            .filter(step -> step.containsKey("uses") && 
                   step.get("uses").toString().contains("actions/setup-java"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(javaSetupStep, "Workflow should have Java setup step");
        
        // Verify Java setup step has 'with' configuration
        assertTrue(javaSetupStep.containsKey("with"), "Java setup step should have 'with' configuration");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> javaConfig = (Map<String, Object>) javaSetupStep.get("with");
        
        // Verify cache is enabled in Java setup
        assertTrue(javaConfig.containsKey("cache"), "Java setup should enable cache");
        String cacheType = (String) javaConfig.get("cache");
        assertEquals("maven", cacheType, "Java setup should use Maven cache");
    }

    @Test
    @DisplayName("Property 16 & 17: Cache configuration is optimized for Maven builds")
    public void testCacheConfigurationOptimization() throws Exception {
        // **Feature: sonarqube-integration, Property 16: Cache restoration & Property 17: Cache creation**
        
        Map<String, Object> workflow = parseWorkflowYaml();
        
        @SuppressWarnings("unchecked")
        Map<String, Object> jobs = (Map<String, Object>) workflow.get("jobs");
        
        String sonarJobName = jobs.keySet().stream()
            .filter(jobName -> jobName.toLowerCase().contains("sonar"))
            .findFirst()
            .orElse("");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> sonarJob = (Map<String, Object>) jobs.get(sonarJobName);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> steps = (List<Map<String, Object>>) sonarJob.get("steps");
        
        // Verify cache step comes before Maven operations
        int cacheStepIndex = -1;
        int mavenStepIndex = -1;
        
        for (int i = 0; i < steps.size(); i++) {
            Map<String, Object> step = steps.get(i);
            
            if (step.containsKey("uses") && step.get("uses").toString().contains("actions/cache")) {
                cacheStepIndex = i;
            }
            
            if (step.containsKey("run") && step.get("run").toString().contains("mvn")) {
                mavenStepIndex = i;
                break; // First Maven command
            }
        }
        
        assertTrue(cacheStepIndex >= 0, "Cache step should exist");
        assertTrue(mavenStepIndex >= 0, "Maven step should exist");
        assertTrue(cacheStepIndex < mavenStepIndex, "Cache step should come before Maven operations");
        
        // Verify Java setup with cache comes before explicit cache step
        int javaSetupIndex = -1;
        for (int i = 0; i < steps.size(); i++) {
            Map<String, Object> step = steps.get(i);
            if (step.containsKey("uses") && step.get("uses").toString().contains("actions/setup-java")) {
                javaSetupIndex = i;
                break;
            }
        }
        
        assertTrue(javaSetupIndex >= 0, "Java setup step should exist");
        assertTrue(javaSetupIndex < cacheStepIndex, "Java setup should come before explicit cache step");
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