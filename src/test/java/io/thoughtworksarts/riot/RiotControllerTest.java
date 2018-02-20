package io.thoughtworksarts.riot;

import io.thoughtworksarts.riot.audio.AudioPlayer;
import io.thoughtworksarts.riot.branching.BranchingLogic;
import io.thoughtworksarts.riot.branching.ConfigRoot;
import io.thoughtworksarts.riot.facialrecognition.FacialRecognitionAPI;
import io.thoughtworksarts.riot.video.MoviePlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

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
    @Mock private MoviePlayer moviePlayer;
    @Mock private FacialRecognitionAPI facialRecognition;
    @Mock private ConfigRoot root;

    @BeforeEach
    void setUp() {
        initMocks(this);
        this.controller = new RiotController(audioPlayer, moviePlayer,
                branchingLogic,
                facialRecognition);
    }

    @Test
    void initRiotShouldCreateLogicTree() throws Exception {
        when(branchingLogic.createLogicTree(anyString())).thenReturn(root);
        when(root.getAudio()).thenReturn("audio file");

        controller.initRiot();

        verify(branchingLogic).createLogicTree(PATH_TO_CONFIG);
        verify(audioPlayer).initialise(DRIVER_NAME, root.getAudio());
        verify(facialRecognition).initialise();
    }

    @Test
    void runRiotShouldResumeAudio() {
        controller.runRiot();

        verify(audioPlayer).resume();
        verify(moviePlayer).play();
    }

    @Test
    void pauseRiotShouldPauseAudio() {
        controller.pauseRiot();

        verify(audioPlayer).pause();
        verify(moviePlayer).pause();
    }

    @Test
    void resumeRiotShouldResumeAudio() {
        controller.resumeRiot();

        verify(audioPlayer).resume();
        verify(moviePlayer).play();
    }

    @Test
    void repeatRiotShouldSeekAudioToTheBeginning() {
        controller.repeatRiot();

        verify(audioPlayer).seek(0.0);
        verify(moviePlayer).seek(0.0);
    }

    @Test
    void endRiotShouldShutdownAudio() {
        controller.endRiot();

        verify(audioPlayer).shutdown();
        verify(moviePlayer).shutdown();
    }
}