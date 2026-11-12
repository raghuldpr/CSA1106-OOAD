package edu.simats.ccrs.domain;

public class Faculty extends User {
    private String department;

    protected Faculty() { }

    public Faculty(String id, String name, String email, String department) {
        super(id, name, email);
        this.department = department;
    }
    public String getDepartment() { return department; }
    @Override public String role() { return "FACULTY"; }
}
