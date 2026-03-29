package com.example.maternity.data.repositories;

import com.example.maternity.domain.MedicalRecord;

import java.util.HashMap;
import java.util.Map;

public class InMemoryMedicalRecordRepository implements MedicalRecordRepository {
    private final Map<String, MedicalRecord> recordsByPatientId = new HashMap<>();

    @Override
    public MedicalRecord findByPatientNationalId(String patientNationalId) {
        return recordsByPatientIdToRecord(patientNationalId);
    }

    private MedicalRecord recordsByPatientIdToRecord(String patientNationalId) {
        return recordsByPatientId.get(patientNationalId);
    }

    @Override
    public void save(MedicalRecord record) {
        recordsByPatientId.put(record.getPatientNationalId(), record);
    }

    @Override
    public boolean existsByPatientNationalId(String patientNationalId) {
        return recordsByPatientId.containsKey(patientNationalId);
    }
}

