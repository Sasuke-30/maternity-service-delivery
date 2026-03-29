package com.example.maternity.business.services;

import com.example.maternity.business.HospitalRegistry;
import com.example.maternity.domain.Patient;
import com.example.maternity.domain.Staff;
import com.example.maternity.exceptions.MaternityException;

import java.time.LocalDate;

public class RegistrationService {
    private final HospitalRegistry registry;

    public RegistrationService(HospitalRegistry registry) {
        this.registry = registry;
    }

    public Patient registerPatient(
            String name,
            int age,
            String nationalId,
            String contactInfo,
            int gestationalAgeWeeks,
            long assignedDoctorId,
            boolean previousComplications
    ) throws MaternityException {
        validateCoreDetails(name, age, nationalId, contactInfo, gestationalAgeWeeks);

        if (registry.getPatientRepository().existsByNationalId(nationalId)) {
            throw new MaternityException.DuplicatePatientException("Patient already registered: " + nationalId);
        }

        Staff assignedDoctor = registry.getStaffRepository().findById(assignedDoctorId);
        if (assignedDoctor == null) {
            throw new MaternityException.NotFoundException("Assigned doctor not found: " + assignedDoctorId);
        }
        if (!(assignedDoctor instanceof Staff.Obstetrician)) {
            throw new MaternityException.ValidationException("Assigned doctor must be an obstetrician");
        }

        Patient patient = new Patient(
                name.trim(),
                age,
                nationalId.trim(),
                contactInfo.trim(),
                gestationalAgeWeeks,
                assignedDoctorId,
                previousComplications,
                LocalDate.now()
        );

        registry.getPatientRepository().addPatient(patient);
        return patient;
    }

    private void validateCoreDetails(String name, int age, String nationalId, String contactInfo, int gestationalAgeWeeks)
            throws MaternityException.ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new MaternityException.ValidationException("Patient name is required");
        }
        if (age <= 0 || age > 130) {
            throw new MaternityException.ValidationException("Patient age must be valid");
        }
        if (nationalId == null || nationalId.trim().isEmpty()) {
            throw new MaternityException.ValidationException("National ID is required");
        }
        if (contactInfo == null || contactInfo.trim().isEmpty()) {
            throw new MaternityException.ValidationException("Contact info is required");
        }
        if (gestationalAgeWeeks < 1 || gestationalAgeWeeks > 45) {
            throw new MaternityException.ValidationException("Gestational age weeks must be between 1 and 45");
        }
    }
}

