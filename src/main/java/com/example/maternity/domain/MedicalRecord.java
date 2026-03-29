package com.example.maternity.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MedicalRecord {
    private final String patientNationalId;
    private final List<AntenatalVisit> antenatalVisits;
    private final List<ClinicalNote> clinicalNotes;
    private DeliveryOutcome deliveryOutcome;

    public MedicalRecord(String patientNationalId) {
        this.patientNationalId = patientNationalId;
        this.antenatalVisits = new ArrayList<>();
        this.clinicalNotes = new ArrayList<>();
    }

    public String getPatientNationalId() {
        return patientNationalId;
    }

    public List<AntenatalVisit> getAntenatalVisits() {
        return Collections.unmodifiableList(antenatalVisits);
    }

    public List<ClinicalNote> getClinicalNotes() {
        return Collections.unmodifiableList(clinicalNotes);
    }

    public DeliveryOutcome getDeliveryOutcome() {
        return deliveryOutcome;
    }

    public void addAntenatalVisit(AntenatalVisit visit) {
        antenatalVisits.add(visit);
    }

    public void addClinicalNote(ClinicalNote note) {
        clinicalNotes.add(note);
    }

    public void setDeliveryOutcome(DeliveryOutcome deliveryOutcome) {
        this.deliveryOutcome = deliveryOutcome;
    }
}

