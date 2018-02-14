package io.thoughtworksarts.riot;

import io.thoughtworksarts.riot.branching.BranchingLogic;
import io.thoughtworksarts.riot.branching.ConfigRoot;
import io.thoughtworksarts.riot.facialrecognition.FacialRecognitionV2API;
import io.thoughtworksarts.riot.sound.AudioPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Timer;

import static io.thoughtworksarts.riot.RiotController.DRIVER_NAME;
import static io.thoughtworksarts.riot.RiotController.PATH_TO_CONFIG;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class RiotControllerTest {

    private RiotController controller;

    @Mock private BranchingLogic branchingLogic;
    @Mock private AudioPlayer audioPlayer;
    @Mock private FacialRecognitionV2API facialRecognition;
    @Mock private ConfigRoot root;
    @Mock private Timer timer;

    @BeforeEach
    void setUp() {
        initMocks(this);
        this.controller = new RiotController(audioPlayer,
                branchingLogic,
                facialRecognition,
                timer);
    }

    @Test
    void initRiotShouldCreateLogicTree() throws Exception {
        when(branchingLogic.createLogicTree(anyString())).thenReturn(root);
        when(root.getAudio()).thenReturn("audio file");

        controller.initRiot();

        verify(branchingLogic).createLogicTree(PATH_TO_CONFIG);
        verify(audioPlayer).initialise(DRIVER_NAME, root.getAudio());
        verify(facialRecognition).Initialise();
    }

    @Test
    void runRiotShouldResumeAudio() {
        controller.runRiot();

        verify(audioPlayer).resume();
    }

    @Test
    void pauseRiotShouldPauseAudio() {
        controller.pauseRiot();

        verify(audioPlayer).pause();
    }

    @Test
    void resumeRiotShouldResumeAudio() {
        controller.resumeRiot();

        verify(audioPlayer).resume();
    }

    @Test
    void repeatRiotShouldSeekAudioToTheBeginning() {
        controller.repeatRiot();

        verify(audioPlayer).seek(0.0);
    }

    @Test
    void endRiotShouldShutdownAudio() {
        controller.endRiot();

        verify(audioPlayer).shutdown();
    }
}