package com.example.maternity.domain;

public abstract class Staff {
    private final long id;
    private final String name;
    private final String contactInfo;

    protected Staff(long id, String name, String contactInfo) {
        this.id = id;
        this.name = name;
        this.contactInfo = contactInfo;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public abstract String getRole();

    public String toString() {
        return String.format("%s[id=%d, name=%s]", getRole(), id, name);
    }

    public static class Midwife extends Staff {
        public Midwife(long id, String name, String contactInfo) {
            super(id, name, contactInfo);
        }

        @Override
        public String getRole() {
            return "MIDWIFE";
        }
    }

    public static class Obstetrician extends Staff {
        public Obstetrician(long id, String name, String contactInfo) {
            super(id, name, contactInfo);
        }

        @Override
        public String getRole() {
            return "OBSTETRICIAN";
        }
    }

    public static class SupportStaff extends Staff {
        public SupportStaff(long id, String name, String contactInfo) {
            super(id, name, contactInfo);
        }

        @Override
        public String getRole() {
            return "SUPPORT_STAFF";
        }
    }
}

