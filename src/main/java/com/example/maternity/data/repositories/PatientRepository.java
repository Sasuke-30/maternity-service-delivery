package com.example.maternity.data.repositories;

import com.example.maternity.domain.Patient;

import java.util.Collection;

public interface PatientRepository {
    void addPatient(Patient patient);

    boolean existsByNationalId(String nationalId);

    Patient findByNationalId(String nationalId);

    Collection<Patient> findAll();
}

