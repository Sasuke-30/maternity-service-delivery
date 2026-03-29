package com.example.maternity.domain;

import java.time.LocalDateTime;

public class AntenatalVisit {
    private final LocalDateTime visitTime;
    private final int systolic;
    private final int diastolic;
    private final String clinicianNotes;

    public AntenatalVisit(LocalDateTime visitTime, int systolic, int diastolic, String clinicianNotes) {
        this.visitTime = visitTime;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.clinicianNotes = clinicianNotes;
    }

    public LocalDateTime getVisitTime() {
        return visitTime;
    }

    public int getSystolic() {
        return systolic;
    }

    public int getDiastolic() {
        return diastolic;
    }

    public String getClinicianNotes() {
        return clinicianNotes;
    }
}

