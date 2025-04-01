package com.ms_cels.report_ms.infrastructure.adapter.output.persistence;

import com.ms_cels.report_ms.infrastructure.adapter.output.persistence.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaPatientRepository extends JpaRepository<PatientEntity, UUID> {
}