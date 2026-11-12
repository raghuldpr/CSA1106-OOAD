package edu.simats.shas;

public enum AppointmentStatus {
    REQUESTED, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW;
    public boolean isActive() { return this == REQUESTED || this == CONFIRMED; }
    public boolean isFinal() { return this == CANCELLED || this == COMPLETED || this == NO_SHOW; }
}
