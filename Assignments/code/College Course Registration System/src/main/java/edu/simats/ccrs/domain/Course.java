package edu.simats.ccrs.domain;

/** A course offering with limited seats. Seat rules live here (information expert). */
public class Course {
    private String code;
    private String title;
    private int credits;
    private int capacity;
    private int enrolled;
    private Faculty instructor;

    protected Course() { }

    public Course(String code, String title, int credits, int capacity, Faculty instructor) {
        if (code == null || !code.matches("[A-Z]{3}\\d{4}")) throw new DomainException("invalid course code: " + code);
        if (credits < 1 || credits > 6) throw new DomainException("credits must be 1..6");
        if (capacity < 1) throw new DomainException("capacity must be positive");
        this.code = code; this.title = title; this.credits = credits;
        this.capacity = capacity; this.instructor = instructor;
    }
    public boolean hasSeat() { return enrolled < capacity; }
    void occupySeat() {
        if (!hasSeat()) throw new DomainException("course " + code + " is full");
        enrolled++;
    }
    void releaseSeat() { if (enrolled > 0) enrolled--; }
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getCredits() { return credits; }
    public int getCapacity() { return capacity; }
    public int getEnrolled() { return enrolled; }
    public Faculty getInstructor() { return instructor; }
}
