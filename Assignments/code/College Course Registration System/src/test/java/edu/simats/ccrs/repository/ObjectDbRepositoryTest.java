package edu.simats.ccrs.repository;

import edu.simats.ccrs.domain.*;
import edu.simats.ccrs.repository.objectdb.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

/** Integration tests against a real ObjectDB database file. */
class ObjectDbRepositoryTest {
    @TempDir Path dir;
    ObjectDbContext db;
    Faculty f = new Faculty("F1", "Dr X", "x@uni.edu", "IT");

    @BeforeEach void open() { db = new ObjectDbContext(dir.resolve("test.odb").toString()); }
    @AfterEach void close() { db.close(); }

    @Test void crudLifecycleOfAStudent() {
        StudentRepository repo = new ObjectDbStudentRepository(db);
        repo.create(new Student("S1", "Asha", "a@uni.edu", "CSE", 3));                 // create + store
        assertEquals("Asha", repo.findById("S1").orElseThrow().getName());             // retrieve
        Student s = repo.findById("S1").orElseThrow();
        s.changeEmail("asha.k@uni.edu"); repo.update(s);                                // modify
        assertEquals("asha.k@uni.edu", repo.findById("S1").orElseThrow().getEmail());
        repo.delete("S1");                                                              // delete
        assertTrue(repo.findById("S1").isEmpty());
    }
    @Test void relationshipStudentRegistrationCourseSurvivesReopeningTheDatabase() {
        new ObjectDbStudentRepository(db).create(new Student("S1", "Asha", "a@uni.edu", "CSE", 3));
        new ObjectDbCourseRepository(db).create(new Course("CSA1106", "OOAD", 4, 60, f));
        Student s = new ObjectDbStudentRepository(db).findById("S1").orElseThrow();
        Course c = new ObjectDbCourseRepository(db).findByCode("CSA1106").orElseThrow();
        new ObjectDbRegistrationRepository(db).create(Registration.create("R0001", s, c));
        db.close();                                                                     // simulate restart
        db = new ObjectDbContext(dir.resolve("test.odb").toString());
        Registration r = new ObjectDbRegistrationRepository(db).findActiveByStudent("S1").get(0);
        assertEquals("Asha", r.getStudent().getName());                                 // navigated reference
        assertEquals("OOAD", r.getCourse().getTitle());
        assertEquals("Dr X", r.getCourse().getInstructor().getName());
        assertEquals(1, new ObjectDbCourseRepository(db).findByCode("CSA1106").orElseThrow().getEnrolled());
    }
    @Test void duplicateObjectIsRejected() {
        StudentRepository repo = new ObjectDbStudentRepository(db);
        repo.create(new Student("S1", "Asha", "a@uni.edu", "CSE", 3));
        assertThrows(RepositoryException.class, () -> repo.create(new Student("S1", "Other", "o@uni.edu", "CSE", 3)));
    }
    @Test void missingObjectAndInvalidIdentifier() {
        StudentRepository repo = new ObjectDbStudentRepository(db);
        assertTrue(repo.findById("NOPE").isEmpty());
        assertThrows(RepositoryException.class, () -> repo.delete("NOPE"));
    }
    @Test void invalidRelationshipIsRejected() {
        new ObjectDbCourseRepository(db).create(new Course("CSA1106", "OOAD", 4, 60, f));
        Course c = new ObjectDbCourseRepository(db).findByCode("CSA1106").orElseThrow();
        Student ghost = new Student("S404", "Ghost", "g@uni.edu", "CSE", 1);           // never stored
        assertThrows(RepositoryException.class,
                () -> new ObjectDbRegistrationRepository(db).create(Registration.create("R0001", ghost, c)));
    }
    @Test void connectionFailureIsTranslated() {
        StudentRepository repo = new ObjectDbStudentRepository(db);
        db.close();                                                                     // database unavailable
        RepositoryException e = assertThrows(RepositoryException.class, () -> repo.findById("S1"));
        assertTrue(e.getMessage().contains("closed"));
    }
}
