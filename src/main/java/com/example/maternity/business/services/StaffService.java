package com.example.maternity.business.services;

import com.example.maternity.business.HospitalRegistry;
import com.example.maternity.domain.Shift;
import com.example.maternity.domain.Staff;
import com.example.maternity.domain.enums.ShiftType;
import com.example.maternity.exceptions.MaternityException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaffService {
    private final HospitalRegistry registry;
    // staffId -> date -> shiftType (prevents duplicate shifts for the same day)
    private final Map<Long, Map<LocalDate, ShiftType>> shifts = new HashMap<>();

    public StaffService(HospitalRegistry registry) {
        this.registry = registry;
    }

    public Staff addMidwife(String name, String contactInfo) throws MaternityException {
        return addStaff(new Staff.Midwife(registry.nextStaffId(), name, contactInfo));
    }

    public Staff addObstetrician(String name, String contactInfo) throws MaternityException {
        return addStaff(new Staff.Obstetrician(registry.nextStaffId(), name, contactInfo));
    }

    public Staff addSupportStaff(String name, String contactInfo) throws MaternityException {
        return addStaff(new Staff.SupportStaff(registry.nextStaffId(), name, contactInfo));
    }

    private Staff addStaff(Staff staff) throws MaternityException {
        if (staff == null) {
            throw new MaternityException.ValidationException("Staff cannot be null");
        }
        registry.getStaffRepository().addStaff(staff);
        return staff;
    }

    public void scheduleShift(long staffId, LocalDate date, ShiftType shiftType) throws MaternityException {
        if (date == null) {
            throw new MaternityException.ValidationException("Date is required");
        }
        if (shiftType == null) {
            throw new MaternityException.ValidationException("Shift type is required");
        }
        if (!registry.getStaffRepository().existsById(staffId)) {
            throw new MaternityException.NotFoundException("Staff not found: " + staffId);
        }

        Map<LocalDate, ShiftType> staffShifts = shifts.computeIfAbsent(staffId, ignored -> new HashMap<>());
        if (staffShifts.containsKey(date)) {
            throw new MaternityException.SchedulingConflictException("Shift already scheduled for staff on that date");
        }
        staffShifts.put(date, shiftType);
    }

    public List<Shift> getShiftsForStaff(long staffId) throws MaternityException {
        if (!registry.getStaffRepository().existsById(staffId)) {
            throw new MaternityException.NotFoundException("Staff not found: " + staffId);
        }
        Map<LocalDate, ShiftType> staffShifts = shifts.getOrDefault(staffId, Map.of());
        List<Shift> result = new ArrayList<>();
        for (Map.Entry<LocalDate, ShiftType> e : staffShifts.entrySet()) {
            result.add(new Shift(staffId, e.getKey(), e.getValue()));
        }
        return result;
    }
}

