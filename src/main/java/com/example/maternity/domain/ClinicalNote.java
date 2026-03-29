package com.example.maternity.domain;

import java.time.LocalDateTime;

public class ClinicalNote {
    private final LocalDateTime noteTime;
    private final String noteText;

    public ClinicalNote(LocalDateTime noteTime, String noteText) {
        this.noteTime = noteTime;
        this.noteText = noteText;
    }

    public LocalDateTime getNoteTime() {
        return noteTime;
    }

    public String getNoteText() {
        return noteText;
    }
}

