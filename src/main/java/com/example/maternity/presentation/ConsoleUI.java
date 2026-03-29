package com.example.maternity.presentation;

import com.example.maternity.business.HospitalRegistry;
import com.example.maternity.business.PatientRecordFactory;
import com.example.maternity.business.services.*;
import com.example.maternity.domain.NewbornDetails;
import com.example.maternity.domain.Patient;
import com.example.maternity.domain.Staff;
import com.example.maternity.domain.Ward;
import com.example.maternity.domain.enums.AppointmentType;
import com.example.maternity.domain.enums.ShiftType;
import com.example.maternity.domain.Appointment;
import com.example.maternity.exceptions.MaternityException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);

    private final HospitalRegistry registry;
    private final RegistrationService registrationService;
    private final AppointmentService appointmentService;
    private final WardService wardService;
    private final StaffService staffService;
    private final RiskAssessmentService riskAssessmentService;
    private final NotificationService notificationService;
    private final PatientRecordFactory recordFactory;
    private final MedicalRecordService medicalRecordService;

    public ConsoleUI() {
        this.registry = HospitalRegistry.getInstance();
        this.registrationService = new RegistrationService(registry);
        this.appointmentService = new AppointmentService(registry);
        this.wardService = new WardService(registry);
        this.staffService = new StaffService(registry);
        this.riskAssessmentService = new RiskAssessmentService();
        this.notificationService = new NotificationService(registry, riskAssessmentService);
        this.recordFactory = new PatientRecordFactory();
        this.medicalRecordService = new MedicalRecordService(registry, recordFactory, notificationService);
    }

    public void run() {
        System.out.println("Maternity Service Delivery System (console simulation)");
        try {
            seedDemoData();
        } catch (MaternityException e) {
            System.out.println("Seed warning: " + e.getMessage());
        }

        while (true) {
            printMenu();
            String choice = prompt("Select option: ").trim();
            try {
                switch (choice) {
                    case "1" -> registerPatient();
                    case "2" -> addWard();
                    case "3" -> addStaff();
                    case "4" -> scheduleShift();
                    case "5" -> admitPatientToWard();
                    case "6" -> dischargePatientFromWard();
                    case "7" -> bookAppointment(AppointmentType.ANTENATAL);
                    case "8" -> bookAppointment(AppointmentType.DELIVERY);
                    case "9" -> bookAppointment(AppointmentType.POSTNATAL);
                    case "10" -> rescheduleAppointment();
                    case "11" -> cancelAppointment();
                    case "12" -> logAntenatalVisit();
                    case "13" -> recordDeliveryOutcome();
                    case "14" -> showPatientSummary();
                    case "0" -> {
                        System.out.println("Exiting.");
                        return;
                    }
                    default -> System.out.println("Unknown option.");
                }
            } catch (MaternityException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IllegalArgumentException | DateTimeParseException e) {
                System.out.println("Input error: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private void seedDemoData() throws MaternityException {
        // Provide a small starter dataset so the user can immediately try the flows.
        // The seed is idempotent for the current singleton runtime.
        if (!registry.getStaffRepository().findAll().isEmpty()) {
            return;
        }

        Staff ob1 = staffService.addObstetrician("Dr. Amina", "amina@hospital.test");
        staffService.addMidwife("Nurse Grace", "grace@hospital.test");
        staffService.addSupportStaff("Support Leo", "leo@hospital.test");

        wardService.addWard("W1", 4);

        System.out.println("Seeded demo data: ward W1, obstetrician " + ob1.getId());
    }

    private void printMenu() {
        System.out.println("---------- Menu ----------");
        System.out.println("1) Register patient");
        System.out.println("2) Add ward");
        System.out.println("3) Add staff");
        System.out.println("4) Schedule staff shift");
        System.out.println("5) Admit patient to ward");
        System.out.println("6) Discharge patient from ward");
        System.out.println("7) Book antenatal appointment");
        System.out.println("8) Book delivery appointment");
        System.out.println("9) Book postnatal appointment");
        System.out.println("10) Reschedule appointment");
        System.out.println("11) Cancel appointment");
        System.out.println("12) Log antenatal visit + alert");
        System.out.println("13) Record delivery outcome + newborn details");
        System.out.println("14) Show patient summary");
        System.out.println("0) Exit");
    }

    private void registerPatient() throws MaternityException {
        System.out.println("== Patient Registration ==");
        String name = prompt("Name: ");
        int age = promptInt("Age: ");
        String nationalId = prompt("National ID: ");
        String contact = prompt("Contact info: ");
        int gestationalWeeks = promptInt("Gestational age (weeks): ");
        long doctorId = promptLong("Assigned doctor ID (obstetrician): ");
        boolean previousComplications = promptBoolean("Previous complications (y/n): ");

        Patient patient = registrationService.registerPatient(
                name,
                age,
                nationalId,
                contact,
                gestationalWeeks,
                doctorId,
                previousComplications
        );
        System.out.println("Registered patient: " + patient.getName() + " (" + patient.getNationalId() + ")");
    }

    private void addWard() {
        System.out.println("== Add Ward ==");
        String code = prompt("Ward code (e.g., W2): ");
        int bedCount = promptInt("Bed count: ");
        wardService.addWard(code, bedCount);
        System.out.println("Ward added: " + code);
    }

    private void addStaff() throws MaternityException {
        System.out.println("== Add Staff ==");
        System.out.println("Select role: 1) Obstetrician  2) Midwife  3) Support Staff");
        String role = prompt("Role: ").trim();
        String name = prompt("Name: ");
        String contact = prompt("Contact info: ");

        Staff staff;
        switch (role) {
            case "1" -> staff = staffService.addObstetrician(name, contact);
            case "2" -> staff = staffService.addMidwife(name, contact);
            case "3" -> staff = staffService.addSupportStaff(name, contact);
            default -> throw new IllegalArgumentException("Invalid role selection");
        }
        System.out.println("Added " + staff.getRole() + " with ID: " + staff.getId());
    }

    private void scheduleShift() throws MaternityException {
        System.out.println("== Schedule Staff Shift ==");
        long staffId = promptLong("Staff ID: ");
        LocalDate date = promptDate("Shift date (yyyy-MM-dd): ");
        System.out.println("Shift type: 1) MORNING  2) AFTERNOON  3) NIGHT");
        String st = prompt("Shift type: ").trim();
        ShiftType shiftType = switch (st) {
            case "1" -> ShiftType.MORNING;
            case "2" -> ShiftType.AFTERNOON;
            case "3" -> ShiftType.NIGHT;
            default -> throw new IllegalArgumentException("Invalid shift type");
        };
        staffService.scheduleShift(staffId, date, shiftType);
        System.out.println("Shift scheduled.");
    }

    private void admitPatientToWard() throws MaternityException {
        System.out.println("== Admit Patient ==");
        String nationalId = prompt("Patient National ID: ");
        String wardCode = prompt("Ward code: ");
        Ward.Bed bed = wardService.admitPatient(wardCode, nationalId);
        System.out.println("Admitted to ward " + wardCode + " on bed " + bed.getBedNumber());
    }

    private void dischargePatientFromWard() throws MaternityException {
        System.out.println("== Discharge Patient ==");
        String nationalId = prompt("Patient National ID: ");
        String wardCode = prompt("Ward code: ");
        LocalDate date = promptDate("Discharge date (yyyy-MM-dd) [blank = today]: ");
        if (date == null) date = LocalDate.now();

        wardService.dischargePatient(wardCode, nationalId, date);
        System.out.println("Discharged patient " + nationalId + " from ward " + wardCode);
    }

    private void bookAppointment(AppointmentType type) throws MaternityException {
        System.out.println("== Book Appointment: " + type + " ==");
        String patientNationalId = prompt("Patient National ID: ");
        LocalDateTime start = promptDateTime("Start time (yyyy-MM-ddTHH:mm): ");
        int durationMinutes = promptInt("Duration (minutes): ");

        Appointment appt = appointmentService.bookAppointment(patientNationalId, type, start, durationMinutes);
        System.out.println("Appointment booked: ID " + appt.getId() + " for doctor " + appt.getDoctorId());
    }

    private void rescheduleAppointment() throws MaternityException {
        System.out.println("== Reschedule Appointment ==");
        long appointmentId = promptLong("Appointment ID: ");
        LocalDateTime start = promptDateTime("New start time (yyyy-MM-ddTHH:mm): ");
        int durationMinutes = promptInt("Duration (minutes): ");

        Appointment appt = appointmentService.rescheduleAppointment(appointmentId, start, durationMinutes);
        System.out.println("Appointment rescheduled: ID " + appt.getId() + " new start " + appt.getStart());
    }

    private void cancelAppointment() throws MaternityException {
        System.out.println("== Cancel Appointment ==");
        long appointmentId = promptLong("Appointment ID: ");
        appointmentService.cancelAppointment(appointmentId);
        System.out.println("Appointment cancelled.");
    }

    private void logAntenatalVisit() throws MaternityException {
        System.out.println("== Log Antenatal Visit ==");
        String patientNationalId = prompt("Patient National ID: ");
        LocalDateTime time = promptDateTime("Visit time (yyyy-MM-ddTHH:mm): ");
        int systolic = promptInt("Systolic BP: ");
        int diastolic = promptInt("Diastolic BP: ");
        String note = prompt("Clinician notes: ");

        List<String> alerts = medicalRecordService.logAntenatalVisit(
                patientNationalId,
                time,
                systolic,
                diastolic,
                note
        );
        if (alerts.isEmpty()) {
            System.out.println("No high-risk alerts triggered.");
        } else {
            System.out.println("High-risk alerts:");
            for (String alert : alerts) {
                System.out.println(" - " + alert);
            }
        }
    }

    private void recordDeliveryOutcome() throws MaternityException {
        System.out.println("== Record Delivery Outcome ==");
        String patientNationalId = prompt("Patient National ID: ");
        LocalDateTime deliveryTime = promptDateTime("Delivery time (yyyy-MM-ddTHH:mm): ");
        String mode = prompt("Delivery mode (e.g., Vaginal, C-SECTION): ");
        String maternalSummary = prompt("Maternal outcome summary: ");
        String babyName = prompt("Newborn name: ");
        String sex = prompt("Newborn sex: ");
        double weightKg = promptDouble("Birth weight (kg): ");
        int apgar = promptInt("Apgar score: ");

        NewbornDetails newborn = new NewbornDetails(babyName, sex, weightKg, apgar);
        medicalRecordService.logDeliveryOutcome(
                patientNationalId,
                deliveryTime,
                mode,
                maternalSummary,
                newborn
        );
        System.out.println("Delivery outcome recorded.");
    }

    private void showPatientSummary() throws MaternityException {
        String nationalId = prompt("Patient National ID: ");

        Patient patient = registry.getPatientRepository().findByNationalId(nationalId);
        if (patient == null) {
            System.out.println("Patient not found.");
            return;
        }

        System.out.println("== Patient Summary ==");
        System.out.println("Name: " + patient.getName());
        System.out.println("Age: " + patient.getAge());
        System.out.println("Gestational (weeks): " + patient.getGestationalAgeWeeks());
        System.out.println("Assigned doctor ID: " + patient.getAssignedDoctorId());
        System.out.println("Previous complications: " + patient.hasPreviousComplications());

        List<Appointment> appointments = registry.getAppointmentRepository().findByPatient(nationalId);
        appointments.sort(Comparator.comparing(Appointment::getStart));
        System.out.println("Appointments (" + appointments.size() + "):");
        for (Appointment a : appointments) {
            System.out.println(" - ID " + a.getId() + " " + a.getType() + " start " + a.getStart() + " status " + a.getStatus());
        }

        // Ward bed status
        for (var ward : registry.getWardRepository().findAll()) {
            for (Map.Entry<Integer, Ward.Bed> e : ward.getBeds().entrySet()) {
                Ward.Bed bed = e.getValue();
                if (!bed.isAvailable() && nationalId.equals(bed.getCurrentPatientNationalId())) {
                    System.out.println("Current bed: ward " + ward.getWardCode() + " bed " + bed.getBedNumber()
                            + " discharge=" + bed.getDischargeDate());
                }
            }
        }

        var record = registry.getMedicalRecordRepository().findByPatientNationalId(nationalId);
        if (record == null) {
            System.out.println("Medical record: not found yet.");
        } else {
            System.out.println("Medical record:");
            System.out.println(" - Antenatal visits: " + record.getAntenatalVisits().size());
            System.out.println(" - Clinical notes: " + record.getClinicalNotes().size());
            System.out.println(" - Delivery outcome: " + (record.getDeliveryOutcome() == null ? "not recorded" : "recorded"));
        }
    }

    private String prompt(String label) {
        System.out.print(label);
        return scanner.nextLine();
    }

    private int promptInt(String label) {
        return Integer.parseInt(prompt(label));
    }

    private long promptLong(String label) {
        return Long.parseLong(prompt(label));
    }

    private double promptDouble(String label) {
        return Double.parseDouble(prompt(label));
    }

    private boolean promptBoolean(String label) {
        String s = prompt(label).trim().toLowerCase();
        return s.startsWith("y") || s.equals("true");
    }

    private LocalDateTime promptDateTime(String label) {
        String s = prompt(label).trim();
        if (s.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(s);
    }

    private LocalDate promptDate(String label) {
        String s = prompt(label).trim();
        if (s.isEmpty()) {
            return null;
        }
        return LocalDate.parse(s);
    }
}

