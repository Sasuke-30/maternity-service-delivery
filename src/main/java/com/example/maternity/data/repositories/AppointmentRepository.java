package com.example.maternity.data.repositories;

import com.example.maternity.domain.Appointment;
import com.example.maternity.domain.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository {
    Appointment save(Appointment appointment);

    Appointment findById(long id);

    List<Appointment> findByDoctor(long doctorId);

    List<Appointment> findConflictingAppointments(long doctorId, LocalDateTime start, LocalDateTime end, AppointmentStatus includeStatus);

    List<Appointment> findByPatient(String patientNationalId);

    void update(Appointment appointment);
}

