package edu.simats.ccrs.domain;

public class Student extends User {
    private String programme;
    private int semester;

    protected Student() { }

    public Student(String id, String name, String email, String programme, int semester) {
        super(id, name, email);
        if (semester < 1 || semester > 8) throw new DomainException("semester must be 1..8");
        this.programme = programme; this.semester = semester;
    }
    public String getProgramme() { return programme; }
    public int getSemester() { return semester; }
    @Override public String role() { return "STUDENT"; }
}
