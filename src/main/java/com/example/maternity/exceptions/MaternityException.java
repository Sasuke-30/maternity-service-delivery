package com.example.maternity.exceptions;

/**
 * Base exception for all maternity ward simulation errors.
 * Uses nested subclasses to keep the codebase compact.
 */
public class MaternityException extends Exception {
    public MaternityException(String message) {
        super(message);
    }

    public static class ValidationException extends MaternityException {
        public ValidationException(String message) {
            super(message);
        }
    }

    public static class NotFoundException extends MaternityException {
        public NotFoundException(String message) {
            super(message);
        }
    }

    public static class DuplicatePatientException extends MaternityException {
        public DuplicatePatientException(String message) {
            super(message);
        }
    }

    public static class SchedulingConflictException extends MaternityException {
        public SchedulingConflictException(String message) {
            super(message);
        }
    }

    public static class NoBedAvailableException extends MaternityException {
        public NoBedAvailableException(String message) {
            super(message);
        }
    }

    public static class InvalidAppointmentStateException extends MaternityException {
        public InvalidAppointmentStateException(String message) {
            super(message);
        }
    }
}

