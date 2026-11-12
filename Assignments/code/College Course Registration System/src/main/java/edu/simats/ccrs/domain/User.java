package edu.simats.ccrs.domain;

/** Abstract superclass of all people who use the system. */
public abstract class User {
    private String id;
    private String name;
    private String email;

    protected User() { }                      // required by the OODBMS

    protected User(String id, String name, String email) {
        if (id == null || id.isBlank()) throw new DomainException("id is required");
        if (name == null || name.isBlank()) throw new DomainException("name is required");
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[a-z]{2,}$"))
            throw new DomainException("invalid e-mail: " + email);
        this.id = id; this.name = name; this.email = email;
    }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public void changeEmail(String newEmail) {
        if (newEmail == null || !newEmail.contains("@")) throw new DomainException("invalid e-mail");
        this.email = newEmail;
    }
    public abstract String role();
}
