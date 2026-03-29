package com.example.maternity.data.repositories;

import com.example.maternity.domain.Patient;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InMemoryPatientRepository implements PatientRepository {
    private final Map<String, Patient> patientsByNationalId = new HashMap<>();

    @Override
    public void addPatient(Patient patient) {
        patientsByNationalId.put(patient.getNationalId(), patient);
    }

    @Override
    public boolean existsByNationalId(String nationalId) {
        return patientsByNationalId.containsKey(nationalId);
    }

    @Override
    public Patient findByNationalId(String nationalId) {
        return patientsByNationalId.get(nationalId);
    }

    @Override
    public Collection<Patient> findAll() {
        return Collections.unmodifiableCollection(patientsByNationalId.values());
    }
}

