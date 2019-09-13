package io.thoughtworksarts.riot.branching;

import io.thoughtworksarts.riot.branching.model.ConfigRoot;
import javafx.util.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonTranslatorTest {

    private ConfigRoot root;
    private JsonTranslator jsonTranslator;

    @BeforeEach
    void setUp() throws Exception {
        jsonTranslator = new JsonTranslator();
        String configFile = "src/test/resources/config.json";
        root = jsonTranslator.populateModelsFromJson(configFile);
    }

    @Test
    void createLogicTreeShouldCreateXMLConfigRoot() throws Exception {
        assertNotNull(root);
        assertEquals(root.getClass(), ConfigRoot.class);
    }

    @Test
    void createLogicTreeShouldContainCorrectPathToVideoFile() throws Exception {
        String videoPath = root.getMedia().getVideo();

        assertTrue(videoPath.contains(".mp4"));
        File file = new File(videoPath);

        try{
            new FileInputStream(file);
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        }
    }

    @Test
    void createLogicTreeShouldContainCorrectPathToAudioFile() throws Exception {
        String audioPath = root.getMedia().getAudio();

        assertTrue(audioPath.contains(".wav"));
        File file = new File(audioPath);

        try{
            new FileInputStream(file);
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        }
    }


    @Test
    void stringToDurationShouldReturnADurationEqualToStringValue() {
        Duration duration = jsonTranslator.convertToDuration("02:03.000");

        assertEquals(duration.toSeconds(), 123.0);
    }

    @Test
    void stringToDurationShouldReturnABeAccurateDownToTheMillisecond() {
        Duration duration = jsonTranslator.convertToDuration("02:34.090");

        assertEquals(duration.toSeconds(), 154.09);
    }

    @Test
    void stringToDurationShouldNotRoundTheMilliseconds() {
        Duration durationOne = jsonTranslator.convertToDuration("02:34.090");
        Duration durationTwo = jsonTranslator.convertToDuration("02:34.091");

        assertNotEquals(durationOne.toSeconds(), durationTwo.toSeconds());
    }

    @Test
    void createLogicTreeShouldContainCorrectModeAttribute() {
        String mode = root.getMode();
        assertEquals(mode, "installation");
    }
}
