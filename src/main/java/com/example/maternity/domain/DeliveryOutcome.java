package com.example.maternity.domain;

import java.time.LocalDateTime;

public class DeliveryOutcome {
    private final LocalDateTime deliveryTime;
    private final String deliveryMode; // e.g., "Vaginal", "C-SECTION"
    private final String maternalOutcomeSummary;
    private final NewbornDetails newbornDetails;

    public DeliveryOutcome(
            LocalDateTime deliveryTime,
            String deliveryMode,
            String maternalOutcomeSummary,
            NewbornDetails newbornDetails
    ) {
        this.deliveryTime = deliveryTime;
        this.deliveryMode = deliveryMode;
        this.maternalOutcomeSummary = maternalOutcomeSummary;
        this.newbornDetails = newbornDetails;
    }

    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public String getMaternalOutcomeSummary() {
        return maternalOutcomeSummary;
    }

    public NewbornDetails getNewbornDetails() {
        return newbornDetails;
    }
}

