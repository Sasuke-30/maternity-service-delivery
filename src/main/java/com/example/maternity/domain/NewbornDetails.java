package com.example.maternity.domain;

public class NewbornDetails {
    private final String babyName;
    private final String sex;
    private final double birthWeightKg;
    private final int apgarScore;

    public NewbornDetails(String babyName, String sex, double birthWeightKg, int apgarScore) {
        this.babyName = babyName;
        this.sex = sex;
        this.birthWeightKg = birthWeightKg;
        this.apgarScore = apgarScore;
    }

    public String getBabyName() {
        return babyName;
    }

    public String getSex() {
        return sex;
    }

    public double getBirthWeightKg() {
        return birthWeightKg;
    }

    public int getApgarScore() {
        return apgarScore;
    }
}

