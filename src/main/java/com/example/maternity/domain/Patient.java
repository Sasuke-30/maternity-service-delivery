package com.example.maternity.domain;

import java.time.LocalDate;

public class Patient {
    private final String name;
    private final int age;
    private final String nationalId;
    private final String contactInfo;
    private final int gestationalAgeWeeks;
    private final long assignedDoctorId;
    private final boolean previousComplications;
    private final LocalDate registeredDate;

    public Patient(
            String name,
            int age,
            String nationalId,
            String contactInfo,
            int gestationalAgeWeeks,
            long assignedDoctorId,
            boolean previousComplications,
            LocalDate registeredDate
    ) {
        this.name = name;
        this.age = age;
        this.nationalId = nationalId;
        this.contactInfo = contactInfo;
        this.gestationalAgeWeeks = gestationalAgeWeeks;
        this.assignedDoctorId = assignedDoctorId;
        this.previousComplications = previousComplications;
        this.registeredDate = registeredDate;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getNationalId() {
        return nationalId;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public int getGestationalAgeWeeks() {
        return gestationalAgeWeeks;
    }

    public long getAssignedDoctorId() {
        return assignedDoctorId;
    }

    public boolean hasPreviousComplications() {
        return previousComplications;
    }

    public LocalDate getRegisteredDate() {
        return registeredDate;
    }
}

