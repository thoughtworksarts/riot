package io.thoughtworksarts.riot.logger;

import org.junit.jupiter.api.Test;

import java.util.logging.Level;


public class LoggerTest {

    @Test
    void testLoggerLogsInCorrectFormat() {
        Level level = Level.INFO;
        String methodLogged = "testLoggerCorrectFormat";
        String message = "this is a test message";
        String[] additionalMessages = {"LEVEL was 1", "TIMESTAMP was 23", "STATE was wbLevel3", "Emotion was Angry"};

        PerceptionLogger logger = new PerceptionLogger("Class under logging");
        logger.log(level, methodLogged, message, additionalMessages);

    }

}
