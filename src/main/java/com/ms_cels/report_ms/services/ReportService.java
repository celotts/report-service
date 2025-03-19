package com.ms_cels.report_ms.services;

import java.util.Map;

public abstract class ReportService {
    public abstract Map<String, Object> makeReport(String id);
    abstract String saveReport(String idReport);
    abstract void deleteReport(String name);
}