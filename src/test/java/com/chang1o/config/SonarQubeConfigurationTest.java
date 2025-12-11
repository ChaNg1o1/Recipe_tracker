package com.chang1o.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * **Feature: sonarqube-integration, Property 15: Project key configuration**
 * 
 * Property-based tests for SonarQube Maven plugin configuration.
 * Validates that the Maven configuration correctly sets up SonarQube properties
 * and plugin configuration as specified in the requirements.
 */
public class SonarQubeConfigurationTest {

    private static final String POM_XML_PATH = "pom.xml";

    @Test
    @DisplayName("Property 15: Project key configuration - For any project configuration, the workflow should set the sonar.projectKey property to uniquely identify the project")
    public void testSonarProjectKeyConfiguration() throws Exception {
        // **Feature: sonarqube-integration, Property 15: Project key configuration**
        
        Document pomDocument = parsePomXml();
        XPath xpath = XPathFactory.newInstance().newXPath();
        
        // Verify sonar.projectKey property is configured
        String projectKeyExpression = "//properties/sonar.projectKey/text()";
        String projectKey = (String) xpath.evaluate(projectKeyExpression, pomDocument, XPathConstants.STRING);
        
        assertNotNull(projectKey, "sonar.projectKey property should be configured in pom.xml");
        assertFalse(projectKey.trim().isEmpty(), "sonar.projectKey should not be empty");
        assertEquals("ChaNg1o1_RecipeTracker", projectKey, "sonar.projectKey should match expected project identifier");
        
        // Verify project key follows expected naming convention
        assertTrue(projectKey.contains("RecipeTracker"), "Project key should contain the project name");
        assertTrue(projectKey.matches("^[a-zA-Z0-9_-]+$"), "Project key should only contain alphanumeric characters, underscores, and hyphens");
    }

    @Test
    @DisplayName("Property 15: SonarQube Maven plugin is properly configured")
    public void testSonarMavenPluginConfiguration() throws Exception {
        // **Feature: sonarqube-integration, Property 15: Project key configuration**
        
        Document pomDocument = parsePomXml();
        XPath xpath = XPathFactory.newInstance().newXPath();
        
        // Verify SonarQube Maven plugin is present
        String pluginExpression = "//plugin[groupId='org.sonarsource.scanner.maven' and artifactId='sonar-maven-plugin']";
        NodeList pluginNodes = (NodeList) xpath.evaluate(pluginExpression, pomDocument, XPathConstants.NODESET);
        
        assertTrue(pluginNodes.getLength() > 0, "SonarQube Maven plugin should be configured in pom.xml");
        
        // Verify plugin version is specified
        String versionExpression = "//plugin[groupId='org.sonarsource.scanner.maven' and artifactId='sonar-maven-plugin']/version/text()";
        String version = (String) xpath.evaluate(versionExpression, pomDocument, XPathConstants.STRING);
        
        assertNotNull(version, "SonarQube Maven plugin version should be specified");
        assertFalse(version.trim().isEmpty(), "SonarQube Maven plugin version should not be empty");
        
        // Check if version is using a property reference
        if (version.startsWith("${") && version.endsWith("}")) {
            // Version is using a property reference, which is valid
            assertEquals("${sonar.version}", version, "Plugin version should reference sonar.version property");
        } else {
            // Version is hardcoded, should follow versioning pattern
            assertTrue(version.matches("\\d+\\.\\d+\\.\\d+\\.\\d+"), "Plugin version should follow four-part versioning pattern (major.minor.patch.build)");
        }
    }

    @Test
    @DisplayName("Property 15: Essential SonarQube properties are configured")
    public void testEssentialSonarQubeProperties() throws Exception {
        // **Feature: sonarqube-integration, Property 15: Project key configuration**
        
        Document pomDocument = parsePomXml();
        XPath xpath = XPathFactory.newInstance().newXPath();
        
        // Test essential properties
        String[] requiredProperties = {
            "sonar.projectKey",
            "sonar.projectName", 
            "sonar.host.url",
            "sonar.organization",
            "sonar.coverage.jacoco.xmlReportPaths",
            "sonar.java.source",
            "sonar.java.target"
        };
        
        for (String property : requiredProperties) {
            String expression = "//properties/" + property + "/text()";
            String value = (String) xpath.evaluate(expression, pomDocument, XPathConstants.STRING);
            
            assertNotNull(value, property + " should be configured in pom.xml");
            assertFalse(value.trim().isEmpty(), property + " should not be empty");
        }
        
        // Verify specific property values
        String hostUrl = (String) xpath.evaluate("//properties/sonar.host.url/text()", pomDocument, XPathConstants.STRING);
        assertEquals("https://sonarcloud.io", hostUrl, "sonar.host.url should point to SonarCloud");
        
        String javaSource = (String) xpath.evaluate("//properties/sonar.java.source/text()", pomDocument, XPathConstants.STRING);
        assertEquals("17", javaSource, "sonar.java.source should be set to Java 17");
        
        String javaTarget = (String) xpath.evaluate("//properties/sonar.java.target/text()", pomDocument, XPathConstants.STRING);
        assertEquals("17", javaTarget, "sonar.java.target should be set to Java 17");
    }

    @Test
    @DisplayName("Property 15: JaCoCo integration properties are correctly configured")
    public void testJaCoCoIntegrationProperties() throws Exception {
        // **Feature: sonarqube-integration, Property 15: Project key configuration**
        
        Document pomDocument = parsePomXml();
        XPath xpath = XPathFactory.newInstance().newXPath();
        
        // Verify JaCoCo XML report path is configured
        String jacocoPath = (String) xpath.evaluate("//properties/sonar.coverage.jacoco.xmlReportPaths/text()", pomDocument, XPathConstants.STRING);
        
        assertNotNull(jacocoPath, "sonar.coverage.jacoco.xmlReportPaths should be configured");
        assertEquals("target/site/jacoco/jacoco.xml", jacocoPath, "JaCoCo XML report path should point to standard Maven target location");
        
        // Verify exclusions are configured
        String exclusions = (String) xpath.evaluate("//properties/sonar.exclusions/text()", pomDocument, XPathConstants.STRING);
        assertNotNull(exclusions, "sonar.exclusions should be configured");
        assertTrue(exclusions.contains("**/target/**"), "Should exclude target directory from analysis");
        assertTrue(exclusions.contains("**/*.sql"), "Should exclude SQL files from analysis");
    }

    private Document parsePomXml() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(POM_XML_PATH);
    }
}