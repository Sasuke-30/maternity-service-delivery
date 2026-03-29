package com.example.maternity.business.services;

import com.example.maternity.business.HospitalRegistry;
import com.example.maternity.domain.Patient;
import com.example.maternity.domain.Ward;
import com.example.maternity.exceptions.MaternityException;

import java.time.LocalDate;

public class WardService {
    private final HospitalRegistry registry;

    public WardService(HospitalRegistry registry) {
        this.registry = registry;
    }

    public void addWard(String wardCode, int bedCount) {
        if (wardCode == null || wardCode.trim().isEmpty()) {
            throw new IllegalArgumentException("wardCode is required");
        }
        if (bedCount <= 0) {
            throw new IllegalArgumentException("bedCount must be > 0");
        }
        if (registry.getWardRepository().findByCode(wardCode) != null) {
            throw new IllegalArgumentException("Ward already exists: " + wardCode);
        }
        registry.getWardRepository().addWard(new Ward(wardCode.trim(), bedCount));
    }

    public Ward.Bed admitPatient(String wardCode, String patientNationalId) throws MaternityException {
        Patient patient = registry.getPatientRepository().findByNationalId(patientNationalId);
        if (patient == null) {
            throw new MaternityException.NotFoundException("Patient not found: " + patientNationalId);
        }

        Ward ward = registry.getWardRepository().findByCode(wardCode);
        if (ward == null) {
            throw new MaternityException.NotFoundException("Ward not found: " + wardCode);
        }

        Ward.Bed bed = ward.admitPatient(patientNationalId);
        if (bed == null) {
            throw new MaternityException.NoBedAvailableException("No beds available in ward " + wardCode);
        }
        return bed;
    }

    public void dischargePatient(String wardCode, String patientNationalId, LocalDate dischargeDate) throws MaternityException {
        Ward ward = registry.getWardRepository().findByCode(wardCode);
        if (ward == null) {
            throw new MaternityException.NotFoundException("Ward not found: " + wardCode);
        }
        if (dischargeDate == null) {
            dischargeDate = LocalDate.now();
        }
        try {
            ward.dischargePatient(patientNationalId, dischargeDate);
        } catch (IllegalStateException e) {
            throw new MaternityException.NotFoundException(e.getMessage());
        }
    }
}

