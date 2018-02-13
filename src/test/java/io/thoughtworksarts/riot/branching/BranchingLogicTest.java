package io.thoughtworksarts.riot.branching;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.jupiter.api.Assertions.*;

class BranchingLogicTest {

    private XmlConfigRoot root;
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
        assertEquals(root.getClass(), XmlConfigRoot.class);
    }

    @Test
    void createLogicTreeShouldContainCorrectPathToVideoFile() throws Exception {
        String videoPath = root.media.video;

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
        String audioPath = root.media.audio;

        assertTrue(root.media.audio.contains(".wav"));
        File file = new File(audioPath);

        try{
            new FileInputStream(file);
        } catch (Exception e){
            assertTrue(false, e.getMessage());
        }
    }

    @Test
    void logicTreeShouldEndIfEmotionIsFear() {
        Emotion fearEmotion = root.levels[0].branch.fear;
        Level fearOutcome = branchingLogic.getOutcome(fearEmotion);

        assertNull(fearOutcome);
    }

    @Test
    void logicTreeShouldEndIfEmotionIsAnger() {
        Emotion angerEmotion = root.levels[0].branch.anger;
        Level angerOutcome = branchingLogic.getOutcome(angerEmotion);

        assertNull(angerOutcome);
    }

    @Test
    void logicTreeShouldContinueIfEmotionIsCalm() {
        Emotion calmEmotion = root.levels[0].branch.calm;
        Level calmOutcome = branchingLogic.getOutcome(calmEmotion);

        assertNotNull(calmOutcome);
        assertTrue(calmOutcome.branch.calm.outcome != 0);
    }

}