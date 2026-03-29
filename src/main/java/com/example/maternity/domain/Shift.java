package com.example.maternity.domain;

import com.example.maternity.domain.enums.ShiftType;

import java.time.LocalDate;

public class Shift {
    private final long staffId;
    private final LocalDate date;
    private final ShiftType shiftType;

    public Shift(long staffId, LocalDate date, ShiftType shiftType) {
        this.staffId = staffId;
        this.date = date;
        this.shiftType = shiftType;
    }

    public long getStaffId() {
        return staffId;
    }

    public LocalDate getDate() {
        return date;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }
}

