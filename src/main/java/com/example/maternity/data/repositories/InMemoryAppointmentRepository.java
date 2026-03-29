package com.example.maternity.data.repositories;

import com.example.maternity.domain.Appointment;
import com.example.maternity.domain.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryAppointmentRepository implements AppointmentRepository {
    private final Map<Long, Appointment> appointmentsById = new HashMap<>();

    @Override
    public Appointment save(Appointment appointment) {
        appointmentsById.put(appointment.getId(), appointment);
        return appointment;
    }

    @Override
    public Appointment findById(long id) {
        return appointmentsById.get(id);
    }

    @Override
    public List<Appointment> findByDoctor(long doctorId) {
        List<Appointment> results = new ArrayList<>();
        for (Appointment a : appointmentsById.values()) {
            if (a.getDoctorId() == doctorId) {
                results.add(a);
            }
        }
        return Collections.unmodifiableList(results);
    }

    @Override
    public List<Appointment> findConflictingAppointments(
            long doctorId,
            LocalDateTime start,
            LocalDateTime end,
            AppointmentStatus includeStatus
    ) {
        List<Appointment> results = new ArrayList<>();
        for (Appointment a : appointmentsById.values()) {
            if (a.getDoctorId() != doctorId) {
                continue;
            }
            if (includeStatus != null && a.getStatus() != includeStatus) {
                continue;
            }
            if (a.overlaps(start, end)) {
                results.add(a);
            }
        }
        return Collections.unmodifiableList(results);
    }

    @Override
    public List<Appointment> findByPatient(String patientNationalId) {
        List<Appointment> results = new ArrayList<>();
        for (Appointment a : appointmentsById.values()) {
            if (a.getPatientNationalId().equals(patientNationalId)) {
                results.add(a);
            }
        }
        return Collections.unmodifiableList(results);
    }

    @Override
    public void update(Appointment appointment) {
        appointmentsById.put(appointment.getId(), appointment);
    }
}

