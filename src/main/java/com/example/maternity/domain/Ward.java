package com.example.maternity.domain;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Ward {
    private final String wardCode;
    private final Map<Integer, Bed> beds;

    public Ward(String wardCode, int bedCount) {
        this.wardCode = wardCode;
        if (bedCount <= 0) {
            throw new IllegalArgumentException("bedCount must be > 0");
        }
        this.beds = new HashMap<>();
        for (int i = 1; i <= bedCount; i++) {
            beds.put(i, new Bed(i));
        }
    }

    public String getWardCode() {
        return wardCode;
    }

    public int getAvailableBedCount() {
        return (int) beds.values().stream().filter(Bed::isAvailable).count();
    }

    public Map<Integer, Bed> getBeds() {
        return Collections.unmodifiableMap(beds);
    }

    public Bed admitPatient(String patientNationalId) {
        for (Bed bed : beds.values()) {
            if (bed.isAvailable()) {
                bed.assignTo(patientNationalId, null);
                return bed;
            }
        }
        return null;
    }

    public void dischargePatient(String patientNationalId, LocalDate dischargeDate) {
        for (Bed bed : beds.values()) {
            if (!bed.isAvailable() && bed.getCurrentPatientNationalId().equals(patientNationalId)) {
                bed.assignTo(null, dischargeDate);
                return;
            }
        }
        throw new IllegalStateException("Patient not found on ward beds: " + patientNationalId);
    }

    public static class Bed {
        private final int bedNumber;
        private boolean available = true;
        private String currentPatientNationalId;
        private LocalDate dischargeDate;

        private Bed(int bedNumber) {
            this.bedNumber = bedNumber;
        }

        public int getBedNumber() {
            return bedNumber;
        }

        public boolean isAvailable() {
            return available;
        }

        public String getCurrentPatientNationalId() {
            return currentPatientNationalId;
        }

        public LocalDate getDischargeDate() {
            return dischargeDate;
        }

        private void assignTo(String patientNationalId, LocalDate dischargeDate) {
            this.currentPatientNationalId = patientNationalId;
            this.dischargeDate = dischargeDate;
            this.available = patientNationalId == null;
        }

        @Override
        public String toString() {
            return String.format("Bed[%d, available=%s, patient=%s, discharge=%s]",
                    bedNumber, available, currentPatientNationalId, dischargeDate);
        }
    }
}

