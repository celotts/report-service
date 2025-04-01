package com.ms_cels.report_ms.application.port.output;

import com.ms_cels.report_ms.models.Patient; // 👈 CAMBIAR AQUÍ
import java.util.Optional;

public interface PatientRepository {
    Optional<Patient> findById(String id);
}