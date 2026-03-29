package com.example.maternity.presentation.api;

import com.example.maternity.business.HospitalRegistry;
import com.example.maternity.business.services.AppointmentService;
import com.example.maternity.business.services.MedicalRecordService;
import com.example.maternity.business.services.RegistrationService;
import com.example.maternity.domain.Appointment;
import com.example.maternity.domain.NewbornDetails;
import com.example.maternity.domain.Patient;
import com.example.maternity.domain.enums.AppointmentType;
import com.example.maternity.exceptions.MaternityException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MaternityController {
    private final RegistrationService registrationService;
    private final AppointmentService appointmentService;
    private final MedicalRecordService medicalRecordService;
    private final HospitalRegistry registry;

    public MaternityController(
            RegistrationService registrationService,
            AppointmentService appointmentService,
            MedicalRecordService medicalRecordService,
            HospitalRegistry registry
    ) {
        this.registrationService = registrationService;
        this.appointmentService = appointmentService;
        this.medicalRecordService = medicalRecordService;
        this.registry = registry;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "maternity-service-delivery");
    }

    @PostMapping("/patients")
    public Patient registerPatient(@RequestBody RegisterPatientRequest req) throws MaternityException {
        return registrationService.registerPatient(
                req.name(),
                req.age(),
                req.nationalId(),
                req.contactInfo(),
                req.gestationalAgeWeeks(),
                req.assignedDoctorId(),
                req.previousComplications()
        );
    }

    @GetMapping("/patients")
    public List<Patient> listPatients() {
        return registry.getPatientRepository().findAll().stream().toList();
    }

    @PostMapping("/appointments")
    public Appointment bookAppointment(@RequestBody AppointmentRequest req) throws MaternityException {
        return appointmentService.bookAppointment(
                req.patientNationalId(),
                req.type(),
                LocalDateTime.parse(req.start()),
                req.durationMinutes()
        );
    }

    @PostMapping("/visits/antenatal")
    public Map<String, Object> logAntenatalVisit(@RequestBody AntenatalVisitRequest req) throws MaternityException {
        List<String> alerts = medicalRecordService.logAntenatalVisit(
                req.patientNationalId(),
                LocalDateTime.parse(req.visitTime()),
                req.systolic(),
                req.diastolic(),
                req.clinicianNotes()
        );
        return Map.of("message", "Visit logged", "alerts", alerts);
    }

    @PostMapping("/delivery")
    public Map<String, String> logDelivery(@RequestBody DeliveryRequest req) throws MaternityException {
        NewbornDetails newborn = new NewbornDetails(
                req.babyName(),
                req.sex(),
                req.birthWeightKg(),
                req.apgarScore()
        );
        medicalRecordService.logDeliveryOutcome(
                req.patientNationalId(),
                LocalDateTime.parse(req.deliveryTime()),
                req.deliveryMode(),
                req.maternalOutcomeSummary(),
                newborn
        );
        return Map.of("message", "Delivery outcome recorded");
    }

    @ExceptionHandler(MaternityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMaternityError(MaternityException ex) {
        return Map.of("error", ex.getMessage());
    }

    public record RegisterPatientRequest(
            String name,
            int age,
            String nationalId,
            String contactInfo,
            int gestationalAgeWeeks,
            long assignedDoctorId,
            boolean previousComplications
    ) {}

    public record AppointmentRequest(
            String patientNationalId,
            AppointmentType type,
            String start,
            int durationMinutes
    ) {}

    public record AntenatalVisitRequest(
            String patientNationalId,
            String visitTime,
            int systolic,
            int diastolic,
            String clinicianNotes
    ) {}

    public record DeliveryRequest(
            String patientNationalId,
            String deliveryTime,
            String deliveryMode,
            String maternalOutcomeSummary,
            String babyName,
            String sex,
            double birthWeightKg,
            int apgarScore
    ) {}
}

