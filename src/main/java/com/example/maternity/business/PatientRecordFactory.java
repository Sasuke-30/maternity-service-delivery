package com.example.maternity.business;

import com.example.maternity.domain.MedicalRecord;

/**
 * Factory for creating medical records for patients.
 */
public class PatientRecordFactory {
    public MedicalRecord createMedicalRecord(String patientNationalId) {
        return new MedicalRecord(patientNationalId);
    }
}

