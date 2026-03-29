package com.example.maternity.business.services;

import com.example.maternity.business.HospitalRegistry;
import com.example.maternity.domain.Appointment;
import com.example.maternity.domain.Patient;
import com.example.maternity.domain.enums.AppointmentStatus;
import com.example.maternity.domain.enums.AppointmentType;
import com.example.maternity.exceptions.MaternityException;

import java.time.LocalDateTime;
import java.util.List;

public class AppointmentService {
    private final HospitalRegistry registry;

    public AppointmentService(HospitalRegistry registry) {
        this.registry = registry;
    }

    public Appointment bookAppointment(
            String patientNationalId,
            AppointmentType type,
            LocalDateTime start,
            int durationMinutes
    ) throws MaternityException {
        if (start == null) {
            throw new MaternityException.ValidationException("Start time is required");
        }
        if (durationMinutes <= 0) {
            throw new MaternityException.ValidationException("Duration must be positive");
        }

        Patient patient = registry.getPatientRepository().findByNationalId(patientNationalId);
        if (patient == null) {
            throw new MaternityException.NotFoundException("Patient not found: " + patientNationalId);
        }

        LocalDateTime end = start.plusMinutes(durationMinutes);

        List<Appointment> conflicts = registry.getAppointmentRepository()
                .findConflictingAppointments(patient.getAssignedDoctorId(), start, end, AppointmentStatus.SCHEDULED);
        if (!conflicts.isEmpty()) {
            throw new MaternityException.SchedulingConflictException(
                    "Doctor is already booked for that time window"
            );
        }

        Appointment appt = new Appointment(
                registry.nextAppointmentId(),
                type,
                patientNationalId,
                patient.getAssignedDoctorId(),
                start,
                end,
                AppointmentStatus.SCHEDULED
        );

        registry.getAppointmentRepository().save(appt);
        return appt;
    }

    public Appointment rescheduleAppointment(
            long appointmentId,
            LocalDateTime newStart,
            int durationMinutes
    ) throws MaternityException {
        if (newStart == null) {
            throw new MaternityException.ValidationException("New start time is required");
        }
        if (durationMinutes <= 0) {
            throw new MaternityException.ValidationException("Duration must be positive");
        }

        Appointment existing = registry.getAppointmentRepository().findById(appointmentId);
        if (existing == null) {
            throw new MaternityException.NotFoundException("Appointment not found: " + appointmentId);
        }
        if (existing.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new MaternityException.InvalidAppointmentStateException("Appointment is not scheduled");
        }

        LocalDateTime newEnd = newStart.plusMinutes(durationMinutes);

        List<Appointment> conflicts = registry.getAppointmentRepository()
                .findConflictingAppointments(existing.getDoctorId(), newStart, newEnd, AppointmentStatus.SCHEDULED);
        conflicts.removeIf(a -> a.getId() == existing.getId());
        if (!conflicts.isEmpty()) {
            throw new MaternityException.SchedulingConflictException(
                    "Doctor is already booked for that time window"
            );
        }

        existing.reschedule(newStart, newEnd);
        registry.getAppointmentRepository().update(existing);
        return existing;
    }

    public void cancelAppointment(long appointmentId) throws MaternityException {
        Appointment existing = registry.getAppointmentRepository().findById(appointmentId);
        if (existing == null) {
            throw new MaternityException.NotFoundException("Appointment not found: " + appointmentId);
        }
        if (existing.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new MaternityException.InvalidAppointmentStateException("Only scheduled appointments can be cancelled");
        }
        existing.setStatus(AppointmentStatus.CANCELLED);
        registry.getAppointmentRepository().update(existing);
    }
}

