package com.example.maternity.business;

import com.example.maternity.data.repositories.*;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Singleton entrypoint for the hospital simulation.
 * Holds repositories (data layer) and shared ID generators.
 */
public class HospitalRegistry {
    private static final HospitalRegistry INSTANCE = new HospitalRegistry();

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final WardRepository wardRepository;
    private final StaffRepository staffRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    private final AtomicLong nextAppointmentId = new AtomicLong(1);
    private final AtomicLong nextStaffId = new AtomicLong(1);
    private final AtomicLong nextShiftId = new AtomicLong(1); // reserved for future extensions

    private HospitalRegistry() {
        this.patientRepository = new InMemoryPatientRepository();
        this.appointmentRepository = new InMemoryAppointmentRepository();
        this.wardRepository = new InMemoryWardRepository();
        this.staffRepository = new InMemoryStaffRepository();
        this.medicalRecordRepository = new InMemoryMedicalRecordRepository();
    }

    public static HospitalRegistry getInstance() {
        return INSTANCE;
    }

    public PatientRepository getPatientRepository() {
        return patientRepository;
    }

    public AppointmentRepository getAppointmentRepository() {
        return appointmentRepository;
    }

    public WardRepository getWardRepository() {
        return wardRepository;
    }

    public StaffRepository getStaffRepository() {
        return staffRepository;
    }

    public MedicalRecordRepository getMedicalRecordRepository() {
        return medicalRecordRepository;
    }

    public long nextAppointmentId() {
        return nextAppointmentId.getAndIncrement();
    }

    public long nextShiftId() {
        return nextShiftId.getAndIncrement();
    }

    public long nextStaffId() {
        return nextStaffId.getAndIncrement();
    }
}

