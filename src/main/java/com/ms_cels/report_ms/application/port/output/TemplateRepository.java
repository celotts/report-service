package com.ms_cels.report_ms.application.port.output;

import java.util.Map;

public interface TemplateRepository {
    String readTemplate();
    String processTemplate(String template, Map<String, Object> data);
}