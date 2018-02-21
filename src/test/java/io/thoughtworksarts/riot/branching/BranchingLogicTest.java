package io.thoughtworksarts.riot.branching;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.jupiter.api.Assertions.*;

class BranchingLogicTest {

    private ConfigRoot root;
    private BranchingLogic branchingLogic;

    @BeforeEach
    void setUp() throws Exception {
        branchingLogic = new BranchingLogic();
        String configFile = "src/main/resources/config.json";
        root = branchingLogic.createLogicTree(configFile);
    }

    @Test
    void createLogicTreeShouldCreateXMLConfigRoot() throws Exception {
        assertNotNull(root);
        assertEquals(root.getClass(), ConfigRoot.class);
    }

    @Test
    void createLogicTreeShouldContainCorrectPathToVideoFile() throws Exception {
        String videoPath = root.getMedia().getVideo();

        assertTrue(videoPath.contains(".m4v"));
        File file = new File(videoPath);

        try{
            new FileInputStream(file);
        } catch (Exception e){
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
        } catch (Exception e){
            assertTrue(false, e.getMessage());
        }
    }

    @Test
    void logicTreeShouldEndIfEmotionIsFear() {
        Emotion fearEmotion = root.getLevels()[0].getBranch().get("fear");
        Level fearOutcome = branchingLogic.getOutcome(fearEmotion);

        assertNull(fearOutcome);
    }

    @Test
    void logicTreeShouldEndIfEmotionIsAnger() {
        Emotion angerEmotion = root.getLevels()[0].getBranch().get("anger");
        Level angerOutcome = branchingLogic.getOutcome(angerEmotion);

        assertNull(angerOutcome);
    }

    @Test
    void logicTreeShouldContinueIfEmotionIsCalm() {
        Emotion calmEmotion = root.getLevels()[0].getBranch().get("calm");
        Level calmOutcome = branchingLogic.getOutcome(calmEmotion);

        assertNotNull(calmOutcome);
        assertTrue(calmOutcome.getBranch().get("calm").getOutcome() != 0);
    }

}