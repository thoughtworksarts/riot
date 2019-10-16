package io.thoughtworksarts.riot.utilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JSONReaderTest {
    @Test
    public void shouldReadFileFromClassPath() {
        String value = JSONReader.readFile("/config/classpath/path/config.json");

        assertEquals("Test data", value);
    }
}