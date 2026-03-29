package com.example.maternity.business.services;

import com.example.maternity.domain.RiskCriteria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RiskAssessmentService {

    /**
     * Produces human-readable risk alerts; empty list means "not flagged".
     */
    public List<String> evaluate(RiskCriteria criteria) {
        if (criteria == null) {
            return Collections.emptyList();
        }

        List<String> alerts = new ArrayList<>();

        if (criteria.getAge() >= 35) {
            alerts.add("Maternal age >= 35 years");
        }

        if (criteria.getSystolic() != null && criteria.getSystolic() >= 140) {
            alerts.add("High systolic blood pressure (>= 140)");
        }

        if (criteria.getDiastolic() != null && criteria.getDiastolic() >= 90) {
            alerts.add("High diastolic blood pressure (>= 90)");
        }

        if (criteria.hasPreviousComplications()) {
            alerts.add("Previous complications reported");
        }

        return alerts;
    }
}

