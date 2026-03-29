package com.example.maternity.domain;

public class RiskCriteria {
    private final int age;
    private final Integer systolic;
    private final Integer diastolic;
    private final boolean previousComplications;

    public RiskCriteria(int age, Integer systolic, Integer diastolic, boolean previousComplications) {
        this.age = age;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.previousComplications = previousComplications;
    }

    public int getAge() {
        return age;
    }

    public Integer getSystolic() {
        return systolic;
    }

    public Integer getDiastolic() {
        return diastolic;
    }

    public boolean hasPreviousComplications() {
        return previousComplications;
    }
}

