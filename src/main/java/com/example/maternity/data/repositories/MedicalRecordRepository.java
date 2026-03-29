package com.example.maternity.data.repositories;

import com.example.maternity.domain.MedicalRecord;

public interface MedicalRecordRepository {
    MedicalRecord findByPatientNationalId(String patientNationalId);

    void save(MedicalRecord record);

    boolean existsByPatientNationalId(String patientNationalId);
}

