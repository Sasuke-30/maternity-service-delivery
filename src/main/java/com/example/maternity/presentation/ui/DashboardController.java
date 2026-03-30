package com.example.maternity.presentation.ui;

import com.example.maternity.business.HospitalRegistry;
import com.example.maternity.business.services.*;
import com.example.maternity.domain.NewbornDetails;
import com.example.maternity.domain.Patient;
import com.example.maternity.domain.Staff;
import com.example.maternity.domain.Ward;
import com.example.maternity.domain.enums.AppointmentType;
import com.example.maternity.domain.enums.ShiftType;
import com.example.maternity.exceptions.MaternityException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class DashboardController {

    private final RegistrationService registrationService;
    private final AppointmentService appointmentService;
    private final WardService wardService;
    private final StaffService staffService;
    private final MedicalRecordService medicalRecordService;
    private final HospitalRegistry registry;

    public DashboardController(
            RegistrationService registrationService,
            AppointmentService appointmentService,
            WardService wardService,
            StaffService staffService,
            MedicalRecordService medicalRecordService,
            HospitalRegistry registry
    ) {
        this.registrationService = registrationService;
        this.appointmentService = appointmentService;
        this.wardService = wardService;
        this.staffService = staffService;
        this.medicalRecordService = medicalRecordService;
        this.registry = registry;
    }

    @GetMapping(value = "/dashboard", produces = MediaType.TEXT_HTML_VALUE)
    public String dashboard() {
        String patientIds = registry.getPatientRepository().findAll().stream()
                .map(Patient::getNationalId)
                .sorted()
                .collect(Collectors.joining(", "));

        String wardSummary = registry.getWardRepository().findAll().stream()
                .map(w -> w.getWardCode() + "(free=" + w.getAvailableBedCount() + ")")
                .sorted()
                .collect(Collectors.joining(", "));

        String staffSummary = registry.getStaffRepository().findAll().stream()
                .map(s -> s.getRole() + ":" + s.getId())
                .sorted()
                .collect(Collectors.joining(", "));

        return page(
                "<h2>Maternity Service Delivery Dashboard</h2>" +
                        "<p>This UI is served by the application itself. It is protected by HTTP Basic Auth (same login as API).</p>" +
                        "<h3>Quick summaries</h3>" +
                        "<p><b>Patients:</b> " + escape(patientIds) + "</p>" +
                        "<p><b>Wards:</b> " + escape(wardSummary) + "</p>" +
                        "<p><b>Staff:</b> " + escape(staffSummary) + "</p>" +
                        "<hr/>" +
                        section("1) Register patient",
                                form("/ui/register", "POST",
                                        input("name", "Name"),
                                        input("age", "Age"),
                                        input("nationalId", "National ID"),
                                        input("contactInfo", "Contact info"),
                                        input("gestationalAgeWeeks", "Gestational age (weeks)"),
                                        input("assignedDoctorId", "Assigned doctor ID (obstetrician)"),
                                        input("previousComplications", "Previous complications (true/false)"),
                                        submit("Register")
                                )) +
                        section("2) Add ward",
                                form("/ui/ward/add", "POST",
                                        input("wardCode", "Ward code (e.g., W2)"),
                                        input("bedCount", "Bed count"),
                                        submit("Add ward")
                                )) +
                        section("3) Add staff",
                                form("/ui/staff/add", "POST",
                                        select("role", "Role",
                                                new String[]{"OBSTETRICIAN", "MIDWIFE", "SUPPORT_STAFF"}
                                        ),
                                        input("name", "Name"),
                                        input("contactInfo", "Contact info"),
                                        submit("Add staff")
                                )) +
                        section("4) Schedule staff shift",
                                form("/ui/staff/shift", "POST",
                                        input("staffId", "Staff ID"),
                                        input("date", "Date (yyyy-MM-dd)"),
                                        select("shiftType", "Shift type",
                                                new String[]{"MORNING", "AFTERNOON", "NIGHT"}
                                        ),
                                        submit("Schedule shift")
                                )) +
                        section("5) Admit patient to ward",
                                form("/ui/ward/admit", "POST",
                                        input("wardCode", "Ward code"),
                                        input("patientNationalId", "Patient National ID"),
                                        submit("Admit")
                                )) +
                        section("6) Discharge patient from ward",
                                form("/ui/ward/discharge", "POST",
                                        input("wardCode", "Ward code"),
                                        input("patientNationalId", "Patient National ID"),
                                        input("dischargeDate", "Discharge date (yyyy-MM-dd) or blank"),
                                        submit("Discharge")
                                )) +
                        section("7) Book appointment (conflict detection enabled)",
                                form("/ui/appointment/book", "POST",
                                        input("patientNationalId", "Patient National ID"),
                                        select("type", "Appointment type", new String[]{"ANTENATAL", "DELIVERY", "POSTNATAL"}),
                                        input("start", "Start time (yyyy-MM-ddTHH:mm)"),
                                        input("durationMinutes", "Duration (minutes)"),
                                        submit("Book")
                                )) +
                        section("8) Log antenatal visit (+ high-risk alerts)",
                                form("/ui/records/antenatal", "POST",
                                        input("patientNationalId", "Patient National ID"),
                                        input("visitTime", "Visit time (yyyy-MM-ddTHH:mm)"),
                                        input("systolic", "Systolic BP"),
                                        input("diastolic", "Diastolic BP"),
                                        input("clinicianNotes", "Clinician notes"),
                                        submit("Log visit")
                                )) +
                        section("9) Record delivery outcome",
                                form("/ui/records/delivery", "POST",
                                        input("patientNationalId", "Patient National ID"),
                                        input("deliveryTime", "Delivery time (yyyy-MM-ddTHH:mm)"),
                                        input("deliveryMode", "Delivery mode (e.g., Vaginal, C-SECTION)"),
                                        input("maternalOutcomeSummary", "Maternal outcome summary"),
                                        input("babyName", "Newborn name"),
                                        input("sex", "Newborn sex"),
                                        input("birthWeightKg", "Birth weight kg"),
                                        input("apgarScore", "Apgar score"),
                                        submit("Record delivery")
                                ))
        );
    }

    @PostMapping(value = "/ui/register", produces = MediaType.TEXT_HTML_VALUE)
    public String register(
            @RequestParam String name,
            @RequestParam int age,
            @RequestParam String nationalId,
            @RequestParam String contactInfo,
            @RequestParam int gestationalAgeWeeks,
            @RequestParam long assignedDoctorId,
            @RequestParam boolean previousComplications
    ) {
        try {
            Patient p = registrationService.registerPatient(
                    name, age, nationalId, contactInfo, gestationalAgeWeeks, assignedDoctorId, previousComplications
            );
            return page("Registered patient: " + escape(p.getName()) + " (" + escape(p.getNationalId()) + ")")
                    + dashboardLinkBlock();
        } catch (MaternityException e) {
            return page("Error: " + escape(e.getMessage())) + dashboardLinkBlock();
        }
    }

    @PostMapping(value = "/ui/ward/add", produces = MediaType.TEXT_HTML_VALUE)
    public String addWard(@RequestParam String wardCode, @RequestParam int bedCount) {
        try {
            wardService.addWard(wardCode, bedCount);
            return page("Ward added: " + escape(wardCode)) + dashboardLinkBlock();
        } catch (Exception e) {
            return page("Error: " + escape(e.getMessage())) + dashboardLinkBlock();
        }
    }

    @PostMapping(value = "/ui/staff/add", produces = MediaType.TEXT_HTML_VALUE)
    public String addStaff(
            @RequestParam String role,
            @RequestParam String name,
            @RequestParam String contactInfo
    ) {
        try {
            Staff staff = switch (role) {
                case "OBSTETRICIAN" -> staffService.addObstetrician(name, contactInfo);
                case "MIDWIFE" -> staffService.addMidwife(name, contactInfo);
                case "SUPPORT_STAFF" -> staffService.addSupportStaff(name, contactInfo);
                default -> throw new IllegalArgumentException("Unknown role: " + role);
            };
            return page("Staff added: " + escape(staff.getRole()) + " ID=" + staff.getId()) + dashboardLinkBlock();
        } catch (MaternityException e) {
            return page("Error: " + escape(e.getMessage())) + dashboardLinkBlock();
        }
    }

    @PostMapping(value = "/ui/staff/shift", produces = MediaType.TEXT_HTML_VALUE)
    public String scheduleShift(
            @RequestParam long staffId,
            @RequestParam String date,
            @RequestParam String shiftType
    ) {
        try {
            LocalDate d = LocalDate.parse(date);
            ShiftType st = ShiftType.valueOf(shiftType);
            staffService.scheduleShift(staffId, d, st);
            return page("Shift scheduled for staffId=" + staffId) + dashboardLinkBlock();
        } catch (Exception e) {
            return page("Error: " + escape(e.getMessage())) + dashboardLinkBlock();
        }
    }

    @PostMapping(value = "/ui/ward/admit", produces = MediaType.TEXT_HTML_VALUE)
    public String admit(
            @RequestParam String wardCode,
            @RequestParam String patientNationalId
    ) {
        try {
            Ward.Bed bed = wardService.admitPatient(wardCode, patientNationalId);
            return page("Admitted patient to ward=" + escape(wardCode) + ", bed=" + bed.getBedNumber())
                    + dashboardLinkBlock();
        } catch (Exception e) {
            return page("Error: " + escape(e.getMessage())) + dashboardLinkBlock();
        }
    }

    @PostMapping(value = "/ui/ward/discharge", produces = MediaType.TEXT_HTML_VALUE)
    public String discharge(
            @RequestParam String wardCode,
            @RequestParam String patientNationalId,
            @RequestParam(required = false) String dischargeDate
    ) {
        try {
            LocalDate date = (dischargeDate == null || dischargeDate.isBlank()) ? null : LocalDate.parse(dischargeDate);
            wardService.dischargePatient(wardCode, patientNationalId, date);
            return page("Discharged patient " + escape(patientNationalId) + " from ward " + escape(wardCode))
                    + dashboardLinkBlock();
        } catch (Exception e) {
            return page("Error: " + escape(e.getMessage())) + dashboardLinkBlock();
        }
    }

    @PostMapping(value = "/ui/appointment/book", produces = MediaType.TEXT_HTML_VALUE)
    public String bookAppointment(
            @RequestParam String patientNationalId,
            @RequestParam String type,
            @RequestParam String start,
            @RequestParam int durationMinutes
    ) {
        try {
            AppointmentType at = AppointmentType.valueOf(type);
            var appt = appointmentService.bookAppointment(
                    patientNationalId,
                    at,
                    LocalDateTime.parse(start),
                    durationMinutes
            );
            return page("Booked appointment ID=" + appt.getId() + " doctorId=" + appt.getDoctorId()
                    + " start=" + appt.getStart()) + dashboardLinkBlock();
        } catch (Exception e) {
            return page("Error: " + escape(e.getMessage())) + dashboardLinkBlock();
        }
    }

    @PostMapping(value = "/ui/records/antenatal", produces = MediaType.TEXT_HTML_VALUE)
    public String antenatal(
            @RequestParam String patientNationalId,
            @RequestParam String visitTime,
            @RequestParam int systolic,
            @RequestParam int diastolic,
            @RequestParam String clinicianNotes
    ) {
        try {
            List<String> alerts = medicalRecordService.logAntenatalVisit(
                    patientNationalId,
                    LocalDateTime.parse(visitTime),
                    systolic,
                    diastolic,
                    clinicianNotes
            );
            String alertsHtml = alerts.isEmpty()
                    ? "<p>No high-risk alerts triggered.</p>"
                    : "<p><b>High-risk alerts:</b></p>" + alerts.stream().map(a -> "<div>- " + escape(a) + "</div>").collect(Collectors.joining());

            return page("Antenatal visit logged for patient " + escape(patientNationalId) + alertsHtml) + dashboardLinkBlock();
        } catch (Exception e) {
            return page("Error: " + escape(e.getMessage())) + dashboardLinkBlock();
        }
    }

    @PostMapping(value = "/ui/records/delivery", produces = MediaType.TEXT_HTML_VALUE)
    public String delivery(
            @RequestParam String patientNationalId,
            @RequestParam String deliveryTime,
            @RequestParam String deliveryMode,
            @RequestParam String maternalOutcomeSummary,
            @RequestParam String babyName,
            @RequestParam String sex,
            @RequestParam double birthWeightKg,
            @RequestParam int apgarScore
    ) {
        try {
            NewbornDetails newborn = new NewbornDetails(babyName, sex, birthWeightKg, apgarScore);
            medicalRecordService.logDeliveryOutcome(
                    patientNationalId,
                    LocalDateTime.parse(deliveryTime),
                    deliveryMode,
                    maternalOutcomeSummary,
                    newborn
            );
            return page("Delivery outcome recorded for patient " + escape(patientNationalId)) + dashboardLinkBlock();
        } catch (Exception e) {
            return page("Error: " + escape(e.getMessage())) + dashboardLinkBlock();
        }
    }

    // ---------- HTML helpers ----------

    private String dashboardLinkBlock() {
        return "<p><a href=\"/dashboard\">Back to dashboard</a></p>";
    }

    private static String page(String bodyHtml) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="utf-8"/>
                    <title>Maternity Dashboard</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 24px; }
                        section { margin-bottom: 24px; padding: 16px; border: 1px solid #ddd; border-radius: 8px; }
                        input, select { margin: 4px 0; width: 320px; padding: 6px; }
                        button { padding: 8px 14px; margin-top: 10px; }
                        hr { margin: 18px 0; }
                    </style>
                </head>
                <body>
                %s
                </body>
                </html>
                """.formatted(bodyHtml);
    }

    private static String section(String title, String contentHtml) {
        return "<section><h3>" + escape(title) + "</h3>" + contentHtml + "</section>";
    }

    private static String form(String action, String method, String... fieldHtml) {
        StringBuilder sb = new StringBuilder();
        sb.append("<form action=\"").append(action).append("\" method=\"").append(method).append("\">");
        for (String f : fieldHtml) sb.append(f);
        sb.append("</form>");
        return sb.toString();
    }

    private static String input(String name, String label) {
        return "<div><label>" + escape(label) + "</label><br/>" +
                "<input type=\"text\" name=\"" + escape(name) + "\"/></div>";
    }

    private static String select(String name, String label, String[] options) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div><label>").append(escape(label)).append("</label><br/>");
        sb.append("<select name=\"").append(escape(name)).append("\">");
        for (String opt : options) {
            sb.append("<option value=\"").append(escape(opt)).append("\">").append(escape(opt)).append("</option>");
        }
        sb.append("</select></div>");
        return sb.toString();
    }

    private static String submit(String label) {
        return "<button type=\"submit\">" + escape(label) + "</button>";
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}

