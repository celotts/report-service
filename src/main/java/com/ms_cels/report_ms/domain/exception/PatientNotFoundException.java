package com.ms_cels.report_ms.domain.exception;


public class PatientNotFoundException extends RuntimeException {

    public PatientNotFoundException(String id) {
            super("No se encontró paciente con ID: " + id);
    }
}
