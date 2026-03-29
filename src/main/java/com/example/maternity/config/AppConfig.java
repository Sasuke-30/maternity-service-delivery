package com.example.maternity.config;

import com.example.maternity.business.HospitalRegistry;
import com.example.maternity.business.PatientRecordFactory;
import com.example.maternity.business.services.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public HospitalRegistry hospitalRegistry() {
        return HospitalRegistry.getInstance();
    }

    @Bean
    public RegistrationService registrationService(HospitalRegistry registry) {
        return new RegistrationService(registry);
    }

    @Bean
    public AppointmentService appointmentService(HospitalRegistry registry) {
        return new AppointmentService(registry);
    }

    @Bean
    public WardService wardService(HospitalRegistry registry) {
        return new WardService(registry);
    }

    @Bean
    public StaffService staffService(HospitalRegistry registry) {
        return new StaffService(registry);
    }

    @Bean
    public RiskAssessmentService riskAssessmentService() {
        return new RiskAssessmentService();
    }

    @Bean
    public NotificationService notificationService(HospitalRegistry registry, RiskAssessmentService riskAssessmentService) {
        return new NotificationService(registry, riskAssessmentService);
    }

    @Bean
    public PatientRecordFactory patientRecordFactory() {
        return new PatientRecordFactory();
    }

    @Bean
    public MedicalRecordService medicalRecordService(
            HospitalRegistry registry,
            PatientRecordFactory factory,
            NotificationService notificationService
    ) {
        return new MedicalRecordService(registry, factory, notificationService);
    }
}

