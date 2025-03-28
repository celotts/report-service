package com.ms_cels.report_ms.helpers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@Slf4j
public class ReportHelper {

    @Value("${report.template}")
    private String reportTemplate;

    public String getReportTemplate() {
        log.info("Report template: {}", reportTemplate);
        return reportTemplate;
    }

    public String readTemplate() {
        try {
            log.info("Leyendo plantilla: {}", this.reportTemplate);
            ClassPathResource resource = new ClassPathResource(this.reportTemplate);
            Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            String template = FileCopyUtils.copyToString(reader);
            log.info("Plantilla cargada correctamente, longitud: {} caracteres", template.length());
            return template;
        } catch (IOException e) {
            log.error("Error al leer la plantilla: {}", e.getMessage(), e);
            return "Error al cargar la plantilla: " + e.getMessage();
        }
    }

    public String processTemplate(String template, Map<String, Object> data) {
        if (template == null || template.isEmpty()) {
            return "No se pudo cargar la plantilla";
        }

        String result = template;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "No especificado";
            result = result.replace(placeholder, value);
        }

        return result;
    }
}