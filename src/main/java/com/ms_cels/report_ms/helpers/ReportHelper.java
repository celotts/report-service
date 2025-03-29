package com.ms_cels.report_ms.helpers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.core.io.ClassPathResource;

@Component
@Slf4j
public class ReportHelper {

    @Value("${report.template}")
    private String reportTemplate;

    @Value("${report.template-url:}")
    private String reportTemplateUrl;


    public String readTemplate() {
        // If URL is configured, use that source first
        if (reportTemplateUrl != null && !reportTemplateUrl.isEmpty()) {
            try {
                log.info("Trying to read template from GitHub: {}", reportTemplateUrl);
                return readTemplateFromGithub();
            } catch (Exception e) {
                log.warn("Error reading from GitHub, trying from classpath: {}", e.getMessage());
                // If it fails, try with the original method
            }
        }

        // Original method that reads from classpath
        try {
            log.info("Reading template from classpath: {}", this.reportTemplate);
            ClassPathResource resource = new ClassPathResource(this.reportTemplate);
            Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            String template = FileCopyUtils.copyToString(reader);
            log.info("Template loaded correctly, length: {} characters", template.length());
            return template;
        } catch (IOException e) {
            log.error("Error reading template from classpath: {}", e.getMessage(), e);
            return "Error loading template: " + e.getMessage();
        }
    }

    private String readTemplateFromGithub() {
        RestTemplate restTemplate = new RestTemplate();
        String yamlContent = restTemplate.getForObject(this.reportTemplateUrl, String.class);

        if (yamlContent == null || yamlContent.isEmpty()) {
            throw new RuntimeException("Could not obtain content of YAML file");
        }

        String template = extractTemplateFromYaml(yamlContent);
        log.info("Template loaded correctly from GitHub, length: {} characters", template.length());
        return template;
    }

    private String extractTemplateFromYaml(String yaml) {
        Pattern pattern = Pattern.compile("template:\\s*\"(.+?)\"");
        Matcher matcher = pattern.matcher(yaml);
        if (matcher.find()) {
            String template = matcher.group(1);

            template = template.replaceAll("\\{name\\+'\\s*'\\+lastName}", "{name} {lastName}");

            log.info("Processed template: {}", template);
            return template;
        }
        log.error("Could not find template in YAML content");
        throw new RuntimeException("Could not extract template from YAML");
    }

    public String processTemplate(String template, Map<String, Object> data) {
        if (template == null || template.isEmpty()) {
            return "Could not load template";
        }

        // Create a normalized map that includes all name variants
        Map<String, Object> normalizedData = new HashMap<>(data);

        // Add name variants (camelCase, lowercase, etc.)
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Add lowercase version if the original key has uppercase letters
            if (!key.equals(key.toLowerCase())) {
                normalizedData.put(key.toLowerCase(), value);
            }

            // If the key is camelCase, add version with underscores (snake_case)
            if (key.matches(".*[A-Z].*") && !key.contains("_")) {
                StringBuilder snakeCase = new StringBuilder();
                for (char c : key.toCharArray()) {
                    if (Character.isUpperCase(c)) {
                        snakeCase.append('_').append(Character.toLowerCase(c));
                    } else {
                        snakeCase.append(c);
                    }
                }
                normalizedData.put(snakeCase.toString(), value);
            }

            // If the key is snake_case, add camelCase version
            if (key.contains("_")) {
                StringBuilder camelCase = new StringBuilder();
                boolean capitalizeNext = false;
                for (char c : key.toCharArray()) {
                    if (c == '_') {
                        capitalizeNext = true;
                    } else {
                        if (capitalizeNext) {
                            camelCase.append(Character.toUpperCase(c));
                            capitalizeNext = false;
                        } else {
                            camelCase.append(c);
                        }
                    }
                }
                normalizedData.put(camelCase.toString(), value);
            }
        }

        log.debug("Normalized data map: {}", normalizedData);

        String result = template;

        // First pass: replace simple expressions {variable}
        for (Map.Entry<String, Object> entry : normalizedData.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "Not specified";
            result = result.replace(placeholder, value);
        }

        // Second pass: process expressions with concatenation
        Pattern pattern = Pattern.compile("\\{(.+?)\\+(.+?)}");
        Matcher matcher = pattern.matcher(result);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String part1 = matcher.group(1).trim();
            String part2 = matcher.group(2).trim();

            // Process first part
            if (part1.startsWith("'") && part1.endsWith("'")) {
                part1 = part1.substring(1, part1.length() - 1); // Remove quotes
            } else {
                String key = part1;
                part1 = normalizedData.containsKey(key) ? normalizedData.get(key).toString() : "Not specified";
            }

            // Process second part
            if (part2.startsWith("'") && part2.endsWith("'")) {
                part2 = part2.substring(1, part2.length() - 1); // Remove quotes
            } else {
                String key = part2;
                part2 = normalizedData.containsKey(key) ? normalizedData.get(key).toString() : "Not specified";
            }

            String replacement = part1 + part2;
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        result = sb.toString();

        return result;
    }
}