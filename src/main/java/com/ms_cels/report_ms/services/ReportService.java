package com.ms_cels.report_ms.services;

public abstract class ReportService {

    abstract String makeReport(String id);
    abstract String saveReport(String idReport);
    void deleteReport(String name) {}

}
