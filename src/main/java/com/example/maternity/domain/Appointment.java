package com.example.maternity.domain;

import com.example.maternity.domain.enums.AppointmentStatus;
import com.example.maternity.domain.enums.AppointmentType;

import java.time.LocalDateTime;

public class Appointment {
    private final long id;
    private final AppointmentType type;
    private final String patientNationalId;
    private final long doctorId;
    private LocalDateTime start;
    private LocalDateTime end;
    private AppointmentStatus status;
    private String notes;

    public Appointment(
            long id,
            AppointmentType type,
            String patientNationalId,
            long doctorId,
            LocalDateTime start,
            LocalDateTime end,
            AppointmentStatus status
    ) {
        if (end.isBefore(start) || end.isEqual(start)) {
            throw new IllegalArgumentException("end must be after start");
        }
        this.id = id;
        this.type = type;
        this.patientNationalId = patientNationalId;
        this.doctorId = doctorId;
        this.start = start;
        this.end = end;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public AppointmentType getType() {
        return type;
    }

    public String getPatientNationalId() {
        return patientNationalId;
    }

    public long getDoctorId() {
        return doctorId;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void reschedule(LocalDateTime newStart, LocalDateTime newEnd) {
        if (newEnd.isBefore(newStart) || newEnd.isEqual(newStart)) {
            throw new IllegalArgumentException("end must be after start");
        }
        this.start = newStart;
        this.end = newEnd;
        this.status = AppointmentStatus.SCHEDULED;
    }

    public boolean overlaps(LocalDateTime otherStart, LocalDateTime otherEnd) {
        // Inclusive/exclusive: treat start inclusive, end exclusive.
        return start.isBefore(otherEnd) && otherStart.isBefore(end);
    }

    @Override
    public String toString() {
        return "Appointment[" +
                "id=" + id +
                ", type=" + type +
                ", patient=" + patientNationalId +
                ", doctorId=" + doctorId +
                ", start=" + start +
                ", end=" + end +
                ", status=" + status +
                ']';
    }
}

