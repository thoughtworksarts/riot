package io.thoughtworksarts.riot.logger;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PerceptionLoggerTest {
    private static Path temporaryFolder;

    @BeforeAll
    public static void setUp() throws IOException {
        temporaryFolder = Files.createTempDirectory(PerceptionLoggerTest.class.getName());
    }

    @Test
    public void shouldCreateDirectoryAndWriteToLogFile() throws IOException {
        new PerceptionLogger("class", temporaryFolder.toString() + "/logs")
                .log(Level.INFO, "method", "message", new String[]{"additional", "entries"});


        final File[] files = temporaryFolder.toFile().listFiles();

        assertNotNull(files);
        assertEquals(1, files.length);

        String firstLine = new BufferedReader(new FileReader(files[0])).readLine();

        assertTrue(firstLine.endsWith("::message::additional::entries"));
        assertTrue(firstLine.startsWith("1::class::method::"));
    }

    @AfterAll
    public static void tearDown() throws IOException {
        FileUtils.deleteDirectory(temporaryFolder.toFile());
    }
}