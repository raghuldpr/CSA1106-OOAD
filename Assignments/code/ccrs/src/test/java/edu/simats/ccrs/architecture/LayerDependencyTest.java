package edu.simats.ccrs.architecture;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import static org.junit.jupiter.api.Assertions.*;

/** Boundary test: scans the import statements of every layer and fails on forbidden dependencies. */
class LayerDependencyTest {
    static final Path SRC = Paths.get("src/main/java/edu/simats/ccrs");

    static List<String> violations(String layer, String... forbidden) throws IOException {
        try (Stream<Path> files = Files.walk(SRC.resolve(layer))) {
            return files.filter(p -> p.toString().endsWith(".java")).flatMap(p -> {
                try {
                    return Files.readAllLines(p).stream().filter(l -> l.startsWith("import"))
                            .filter(l -> Arrays.stream(forbidden).anyMatch(l::contains))
                            .map(l -> p.getFileName() + ": " + l);
                } catch (IOException e) { throw new java.io.UncheckedIOException(e); }
            }).collect(Collectors.toList());
        }
    }
    @Test void viewDoesNotAccessRepositoriesOrTheDatabase() throws IOException {
        assertEquals(List.of(), violations("view", ".repository", "jakarta.persistence", "com.objectdb", ".domain", ".service"));
    }
    @Test void domainDoesNotDependOnUiServicesOrPersistence() throws IOException {
        assertEquals(List.of(), violations("domain", ".view", ".controller", ".service", ".repository", "jakarta.persistence", "com.objectdb"));
    }
    @Test void servicesContainNoDatabaseSpecificCode() throws IOException {
        assertEquals(List.of(), violations("service", "jakarta.persistence", "com.objectdb", ".repository.objectdb", ".view"));
    }
    @Test void controllerDoesNotTouchRepositories() throws IOException {
        assertEquals(List.of(), violations("controller", ".repository.objectdb", "jakarta.persistence", ".view"));
    }
}
