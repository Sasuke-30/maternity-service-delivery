package com.example.maternity.business.services;

import com.example.maternity.business.HospitalRegistry;
import com.example.maternity.business.PatientRecordFactory;
import com.example.maternity.domain.AntenatalVisit;
import com.example.maternity.domain.ClinicalNote;
import com.example.maternity.domain.DeliveryOutcome;
import com.example.maternity.domain.MedicalRecord;
import com.example.maternity.domain.NewbornDetails;
import com.example.maternity.domain.Patient;
import com.example.maternity.exceptions.MaternityException;

import java.time.LocalDateTime;
import java.util.List;

public class MedicalRecordService {
    private final HospitalRegistry registry;
    private final PatientRecordFactory recordFactory;
    private final NotificationService notificationService;

    public MedicalRecordService(
            HospitalRegistry registry,
            PatientRecordFactory recordFactory,
            NotificationService notificationService
    ) {
        this.registry = registry;
        this.recordFactory = recordFactory;
        this.notificationService = notificationService;
    }

    public List<String> logAntenatalVisit(
            String patientNationalId,
            LocalDateTime visitTime,
            int systolic,
            int diastolic,
            String clinicianNotes
    ) throws MaternityException {
        Patient patient = registry.getPatientRepository().findByNationalId(patientNationalId);
        if (patient == null) {
            throw new MaternityException.NotFoundException("Patient not found: " + patientNationalId);
        }
        if (visitTime == null) {
            throw new MaternityException.ValidationException("visitTime is required");
        }
        if (systolic <= 0 || diastolic <= 0) {
            throw new MaternityException.ValidationException("Blood pressure readings must be positive");
        }

        MedicalRecord record = getOrCreateRecord(patientNationalId);

        record.addAntenatalVisit(new AntenatalVisit(visitTime, systolic, diastolic, clinicianNotes));
        record.addClinicalNote(new ClinicalNote(visitTime, "Antenatal visit: BP " + systolic + "/" + diastolic));
        registry.getMedicalRecordRepository().save(record);

        // High-risk alerts are produced from the new readings.
        return notificationService.checkHighRisk(patientNationalId, systolic, diastolic);
    }

    public void logDeliveryOutcome(
            String patientNationalId,
            LocalDateTime deliveryTime,
            String deliveryMode,
            String maternalOutcomeSummary,
            NewbornDetails newbornDetails
    ) throws MaternityException {
        if (deliveryTime == null) {
            throw new MaternityException.ValidationException("deliveryTime is required");
        }
        if (newbornDetails == null) {
            throw new MaternityException.ValidationException("newbornDetails is required");
        }
        Patient patient = registry.getPatientRepository().findByNationalId(patientNationalId);
        if (patient == null) {
            throw new MaternityException.NotFoundException("Patient not found: " + patientNationalId);
        }

        MedicalRecord record = getOrCreateRecord(patientNationalId);
        DeliveryOutcome outcome = new DeliveryOutcome(deliveryTime, deliveryMode, maternalOutcomeSummary, newbornDetails);
        record.setDeliveryOutcome(outcome);
        record.addClinicalNote(new ClinicalNote(deliveryTime, "Delivery outcome recorded"));
        registry.getMedicalRecordRepository().save(record);
    }

    public void addClinicalNote(String patientNationalId, LocalDateTime noteTime, String noteText) throws MaternityException {
        if (noteTime == null) {
            throw new MaternityException.ValidationException("noteTime is required");
        }
        if (noteText == null || noteText.trim().isEmpty()) {
            throw new MaternityException.ValidationException("noteText is required");
        }
        Patient patient = registry.getPatientRepository().findByNationalId(patientNationalId);
        if (patient == null) {
            throw new MaternityException.NotFoundException("Patient not found: " + patientNationalId);
        }

        MedicalRecord record = getOrCreateRecord(patientNationalId);
        record.addClinicalNote(new ClinicalNote(noteTime, noteText));
        registry.getMedicalRecordRepository().save(record);
    }

    private MedicalRecord getOrCreateRecord(String patientNationalId) {
        if (registry.getMedicalRecordRepository().existsByPatientNationalId(patientNationalId)) {
            MedicalRecord existing = registry.getMedicalRecordRepository().findByPatientNationalId(patientNationalId);
            if (existing != null) {
                return existing;
            }
        }
        MedicalRecord created = recordFactory.createMedicalRecord(patientNationalId);
        registry.getMedicalRecordRepository().save(created);
        return created;
    }
}

