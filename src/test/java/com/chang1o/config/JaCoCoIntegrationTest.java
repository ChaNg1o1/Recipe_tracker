package com.chang1o.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * **Feature: sonarqube-integration, Property 4: JaCoCo integration execution**
 * 
 * Property-based tests for JaCoCo integration with SonarQube.
 * Validates that Maven tests execute with JaCoCo coverage enabled and
 * that SonarQube is properly configured to consume JaCoCo reports.
 */
public class JaCoCoIntegrationTest {

    private static final String POM_XML_PATH = "pom.xml";
    private static final String JACOCO_XML_REPORT_PATH = "target/site/jacoco/jacoco.xml";

    @Test
    @DisplayName("Property 4: JaCoCo integration execution - For any workflow run, Maven tests should execute with JaCoCo coverage enabled")
    public void testJaCoCoPluginConfiguration() throws Exception {
        // **Feature: sonarqube-integration, Property 4: JaCoCo integration execution**
        
        Document pomDocument = parsePomXml();
        XPath xpath = XPathFactory.newInstance().newXPath();
        
        // Verify JaCoCo Maven plugin is present
        String pluginExpression = "//plugin[groupId='org.jacoco' and artifactId='jacoco-maven-plugin']";
        NodeList pluginNodes = (NodeList) xpath.evaluate(pluginExpression, pomDocument, XPathConstants.NODESET);
        
        assertTrue(pluginNodes.getLength() > 0, "JaCoCo Maven plugin should be configured in pom.xml");
        
        // Verify JaCoCo plugin version is specified
        String versionExpression = "//plugin[groupId='org.jacoco' and artifactId='jacoco-maven-plugin']/version/text()";
        String version = (String) xpath.evaluate(versionExpression, pomDocument, XPathConstants.STRING);
        
        assertNotNull(version, "JaCoCo Maven plugin version should be specified");
        assertFalse(version.trim().isEmpty(), "JaCoCo Maven plugin version should not be empty");
        
        // Check if version is using a property reference
        if (version.startsWith("${") && version.endsWith("}")) {
            assertEquals("${jacoco.version}", version, "JaCoCo plugin version should reference jacoco.version property");
        } else {
            assertTrue(version.matches("\\d+\\.\\d+\\.\\d+"), "JaCoCo plugin version should follow semantic versioning pattern");
        }
    }

    @Test
    @DisplayName("Property 4: JaCoCo executions are properly configured")
    public void testJaCoCoExecutions() throws Exception {
        // **Feature: sonarqube-integration, Property 4: JaCoCo integration execution**
        
        Document pomDocument = parsePomXml();
        XPath xpath = XPathFactory.newInstance().newXPath();
        
        // Verify prepare-agent execution exists
        String prepareAgentExpression = "//plugin[groupId='org.jacoco' and artifactId='jacoco-maven-plugin']//execution[goals/goal='prepare-agent']";
        NodeList prepareAgentNodes = (NodeList) xpath.evaluate(prepareAgentExpression, pomDocument, XPathConstants.NODESET);
        
        assertTrue(prepareAgentNodes.getLength() > 0, "JaCoCo prepare-agent execution should be configured");
        
        // Verify report execution exists
        String reportExpression = "//plugin[groupId='org.jacoco' and artifactId='jacoco-maven-plugin']//execution[goals/goal='report']";
        NodeList reportNodes = (NodeList) xpath.evaluate(reportExpression, pomDocument, XPathConstants.NODESET);
        
        assertTrue(reportNodes.getLength() > 0, "JaCoCo report execution should be configured");
        
        // Verify report execution is bound to test phase
        String reportPhaseExpression = "//plugin[groupId='org.jacoco' and artifactId='jacoco-maven-plugin']//execution[goals/goal='report']/phase/text()";
        String reportPhase = (String) xpath.evaluate(reportPhaseExpression, pomDocument, XPathConstants.STRING);
        
        assertEquals("test", reportPhase, "JaCoCo report execution should be bound to test phase");
    }

    @Test
    @DisplayName("Property 4: SonarQube JaCoCo integration properties are configured")
    public void testSonarQubeJaCoCoIntegration() throws Exception {
        // **Feature: sonarqube-integration, Property 4: JaCoCo integration execution**
        
        Document pomDocument = parsePomXml();
        XPath xpath = XPathFactory.newInstance().newXPath();
        
        // Verify SonarQube JaCoCo XML report path is configured
        String jacocoPathExpression = "//properties/sonar.coverage.jacoco.xmlReportPaths/text()";
        String jacocoPath = (String) xpath.evaluate(jacocoPathExpression, pomDocument, XPathConstants.STRING);
        
        assertNotNull(jacocoPath, "sonar.coverage.jacoco.xmlReportPaths should be configured");
        assertEquals("target/site/jacoco/jacoco.xml", jacocoPath, "JaCoCo XML report path should point to standard Maven target location");
        
        // Verify the path matches the expected JaCoCo output location
        assertTrue(jacocoPath.contains("jacoco.xml"), "JaCoCo report path should reference jacoco.xml file");
        assertTrue(jacocoPath.startsWith("target/"), "JaCoCo report path should be in target directory");
    }

    @Test
    @DisplayName("Property 4: JaCoCo version property is defined")
    public void testJaCoCoVersionProperty() throws Exception {
        // **Feature: sonarqube-integration, Property 4: JaCoCo integration execution**
        
        Document pomDocument = parsePomXml();
        XPath xpath = XPathFactory.newInstance().newXPath();
        
        // Verify jacoco.version property is defined
        String versionExpression = "//properties/jacoco.version/text()";
        String version = (String) xpath.evaluate(versionExpression, pomDocument, XPathConstants.STRING);
        
        assertNotNull(version, "jacoco.version property should be defined in pom.xml");
        assertFalse(version.trim().isEmpty(), "jacoco.version should not be empty");
        assertTrue(version.matches("\\d+\\.\\d+\\.\\d+"), "JaCoCo version should follow semantic versioning pattern");
        
        // Verify it's a reasonable version (0.8.x or higher)
        String[] versionParts = version.split("\\.");
        int majorVersion = Integer.parseInt(versionParts[0]);
        int minorVersion = Integer.parseInt(versionParts[1]);
        
        assertTrue(majorVersion >= 0, "JaCoCo major version should be 0 or higher");
        if (majorVersion == 0) {
            assertTrue(minorVersion >= 8, "JaCoCo minor version should be 8 or higher for version 0.x");
        }
    }

    @Test
    @DisplayName("Property 4: JaCoCo coverage thresholds are configured")
    public void testJaCoCoCoverageThresholds() throws Exception {
        // **Feature: sonarqube-integration, Property 4: JaCoCo integration execution**
        
        Document pomDocument = parsePomXml();
        XPath xpath = XPathFactory.newInstance().newXPath();
        
        // Verify check execution exists (for coverage thresholds)
        String checkExpression = "//plugin[groupId='org.jacoco' and artifactId='jacoco-maven-plugin']//execution[goals/goal='check']";
        NodeList checkNodes = (NodeList) xpath.evaluate(checkExpression, pomDocument, XPathConstants.NODESET);
        
        assertTrue(checkNodes.getLength() > 0, "JaCoCo check execution should be configured for coverage thresholds");
        
        // Verify coverage rules are configured
        String rulesExpression = "//plugin[groupId='org.jacoco' and artifactId='jacoco-maven-plugin']//execution[goals/goal='check']//rules/rule";
        NodeList rulesNodes = (NodeList) xpath.evaluate(rulesExpression, pomDocument, XPathConstants.NODESET);
        
        assertTrue(rulesNodes.getLength() > 0, "JaCoCo coverage rules should be configured");
        
        // Verify minimum coverage threshold is set
        String minimumExpression = "//plugin[groupId='org.jacoco' and artifactId='jacoco-maven-plugin']//execution[goals/goal='check']//limit/minimum/text()";
        String minimum = (String) xpath.evaluate(minimumExpression, pomDocument, XPathConstants.STRING);
        
        assertNotNull(minimum, "JaCoCo minimum coverage threshold should be configured");
        assertFalse(minimum.trim().isEmpty(), "JaCoCo minimum coverage threshold should not be empty");
        
        // Verify it's a reasonable threshold (between 0.0 and 1.0)
        double threshold = Double.parseDouble(minimum);
        assertTrue(threshold >= 0.0 && threshold <= 1.0, "Coverage threshold should be between 0.0 and 1.0");
    }

    private Document parsePomXml() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(POM_XML_PATH);
    }
}