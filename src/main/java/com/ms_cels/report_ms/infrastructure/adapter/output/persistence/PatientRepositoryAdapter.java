package com.ms_cels.report_ms.infrastructure.adapter.output.persistence;

import com.ms_cels.report_ms.application.port.output.PatientRepository;
import com.ms_cels.report_ms.models.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PatientRepositoryAdapter implements PatientRepository {

    private final com.ms_cels.report_ms.repositories.PatientRepository feignPatientRepository;

    @Override
    public Optional<Patient> findById(String id) {
        return feignPatientRepository.getById(id);
    }
}