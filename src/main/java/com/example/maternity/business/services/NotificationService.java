package com.example.maternity.business.services;

import com.example.maternity.business.HospitalRegistry;
import com.example.maternity.domain.Patient;
import com.example.maternity.domain.RiskCriteria;

import java.util.List;

public class NotificationService {
    private final HospitalRegistry registry;
    private final RiskAssessmentService riskAssessmentService;

    public NotificationService(HospitalRegistry registry, RiskAssessmentService riskAssessmentService) {
        this.registry = registry;
        this.riskAssessmentService = riskAssessmentService;
    }

    public List<String> checkHighRisk(String patientNationalId, Integer systolic, Integer diastolic) {
        Patient patient = registry.getPatientRepository().findByNationalId(patientNationalId);
        if (patient == null) {
            throw new IllegalArgumentException("Unknown patient: " + patientNationalId);
        }

        RiskCriteria criteria = new RiskCriteria(
                patient.getAge(),
                systolic,
                diastolic,
                patient.hasPreviousComplications()
        );
        return riskAssessmentService.evaluate(criteria);
    }
}

