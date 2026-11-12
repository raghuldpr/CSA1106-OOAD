package edu.simats.ccrs;

import edu.simats.ccrs.controller.*;
import edu.simats.ccrs.domain.rules.*;
import edu.simats.ccrs.repository.objectdb.*;
import edu.simats.ccrs.service.*;
import edu.simats.ccrs.view.ConsoleView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.*;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/** VIEW -> CONTROLLER -> SERVICE -> DOMAIN -> REPOSITORY -> OODBMS and back. */
class EndToEndTest {
    @TempDir Path dir;

    @Test void studentRegistersThroughTheViewAndTheRegistrationIsPersisted() throws Exception {
        String path = dir.resolve("e2e.odb").toString();
        try (ObjectDbContext db = new ObjectDbContext(path)) {
            App.seed(new ObjectDbCourseRepository(db));
            var rs = new RegistrationService(new ObjectDbStudentRepository(db), new ObjectDbCourseRepository(db),
                    new ObjectDbRegistrationRepository(db), List.of(new SeatAvailableRule(), new NoDuplicateRule()));
            var controller = new RegistrationController(new StudentService(new ObjectDbStudentRepository(db)), rs);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            new ConsoleView(controller, new PrintStream(buf, true)).run(new BufferedReader(new StringReader(
                    "enrol|S1|Asha|a@uni.edu|CSE|3\nregister|S1|CSA1106\n")));
            assertTrue(buf.toString().contains("OK    Registration R0001: S1 -> CSA1106 (seats 1/60)"));
        }
        try (ObjectDbContext db = new ObjectDbContext(path)) {                          // fresh process view
            assertEquals("CSA1106", new ObjectDbRegistrationRepository(db).findActiveByStudent("S1").get(0).getCourse().getCode());
        }
    }
}
