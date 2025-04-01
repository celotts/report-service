package com.ms_cels.report_ms.infrastructure.adapter.input;

import com.ms_cels.report_ms.application.port.output.PatientRepository;
import com.ms_cels.report_ms.domain.model.Patient;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PatientRepositoryAdapter implements PatientRepository {
    private final com.ms_cels.report_ms.repositories.PatientRepository feignPatientRepository;

    public PatientRepositoryAdapter(com.ms_cels.report_ms.repositories.PatientRepository feignPatientRepository) {
        this.feignPatientRepository = feignPatientRepository;
    }

    @Override
    public Optional<Patient> findById(String id) {
        return feignPatientRepository.getById(id)
                .map(this::convertToDomainPatient);
    }

    private Patient convertToDomainPatient(com.ms_cels.report_ms.models.Patient servicePatient) {
        Patient domainPatient = new Patient();

        domainPatient.setId(servicePatient.getId());
        domainPatient.setFirstName(servicePatient.getFirstName());
        domainPatient.setLastName(servicePatient.getLastName());
        // Mapea todos los campos necesarios...

        return domainPatient;
    }
}