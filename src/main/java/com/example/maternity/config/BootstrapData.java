package com.example.maternity.config;

import com.example.maternity.business.services.StaffService;
import com.example.maternity.business.services.WardService;
import com.example.maternity.exceptions.MaternityException;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class BootstrapData {
    private final StaffService staffService;
    private final WardService wardService;

    public BootstrapData(StaffService staffService, WardService wardService) {
        this.staffService = staffService;
        this.wardService = wardService;
    }

    @PostConstruct
    public void init() throws MaternityException {
        if (staffService != null) {
            staffService.addObstetrician("Dr. Amina", "amina@hospital.test");
            staffService.addMidwife("Nurse Grace", "grace@hospital.test");
            staffService.addSupportStaff("Support Leo", "leo@hospital.test");
        }
        wardService.addWard("W1", 8);
    }
}

