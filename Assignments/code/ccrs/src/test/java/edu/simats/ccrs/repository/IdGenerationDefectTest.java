package edu.simats.ccrs.repository;

import edu.simats.ccrs.domain.*;
import edu.simats.ccrs.repository.objectdb.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

/** Regression test for defect D2: identifiers must stay unique after a registration is deleted. */
class IdGenerationDefectTest {
    @TempDir Path dir;

    @Test void nextIdIsUniqueAfterADelete() {
        try (ObjectDbContext db = new ObjectDbContext(dir.resolve("id.odb").toString())) {
            Faculty f = new Faculty("F1", "Dr X", "x@uni.edu", "IT");
            new ObjectDbStudentRepository(db).create(new Student("S1", "Asha", "a@uni.edu", "CSE", 3));
            CourseRepository courses = new ObjectDbCourseRepository(db);
            courses.create(new Course("CSA1106", "OOAD", 4, 60, f));
            courses.create(new Course("CSA1201", "DBMS", 4, 60, f));
            RegistrationRepository regs = new ObjectDbRegistrationRepository(db);
            Student s = new ObjectDbStudentRepository(db).findById("S1").orElseThrow();
            regs.create(Registration.create(regs.nextId(), s, courses.findByCode("CSA1106").orElseThrow())); // R0001
            regs.create(Registration.create(regs.nextId(), s, courses.findByCode("CSA1201").orElseThrow())); // R0002
            regs.delete("R0001");
            String next = regs.nextId();
            assertTrue(regs.findById(next).isEmpty(), "nextId() returned an identifier that already exists: " + next);
        }
    }
}
