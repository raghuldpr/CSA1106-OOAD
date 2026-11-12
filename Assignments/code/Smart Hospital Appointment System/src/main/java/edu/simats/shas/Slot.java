package edu.simats.shas;

import java.time.LocalDateTime;

/** A doctor's time slot. BR1: a slot can be held by only one appointment. */
public class Slot {
    private final String slotId;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private boolean available = true;

    public Slot(String slotId, LocalDateTime startTime, LocalDateTime endTime) {
        if (!endTime.isAfter(startTime)) throw new IllegalArgumentException("slot must end after it starts");
        this.slotId = slotId; this.startTime = startTime; this.endTime = endTime;
    }
    /** Holds the slot; refuses if it is already held (BR1). */
    public void hold() {
        if (!available) throw new BusinessRuleException("BR1: slot " + slotId + " is already held");
        available = false;
    }
    public void release() { available = true; }
    public boolean isAvailable() { return available; }
    public String getSlotId() { return slotId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
}
