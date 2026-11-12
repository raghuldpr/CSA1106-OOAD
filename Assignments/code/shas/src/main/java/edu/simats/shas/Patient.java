package edu.simats.shas;

/** Minimal patient: identity, contact data, preferred channel and active-appointment count (BR2). */
public class Patient {
    public static final int MAX_ACTIVE = 3;
    private final String patientId;
    private final String phone;
    private final String email;
    private final Channel preferredChannel;
    private int activeCount;

    public Patient(String patientId, String phone, String email, Channel preferredChannel) {
        this.patientId = patientId; this.phone = phone; this.email = email;
        this.preferredChannel = preferredChannel;
    }
    void addActive() {
        if (activeCount >= MAX_ACTIVE)
            throw new BusinessRuleException("BR2: patient " + patientId + " already has " + MAX_ACTIVE + " active appointments");
        activeCount++;
    }
    void removeActive() {
        if (activeCount == 0) throw new IllegalStateException("active count already zero");
        activeCount--;
    }
    public int getActiveCount() { return activeCount; }
    public String getPatientId() { return patientId; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public Channel getPreferredChannel() { return preferredChannel; }
}
