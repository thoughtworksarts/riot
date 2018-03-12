package io.thoughtworksarts.riot.functional;

import io.thoughtworksarts.riot.audio.AudioPlayer;
import io.thoughtworksarts.riot.audio.JavaSoundAudioPlayer;
import io.thoughtworksarts.riot.audio.RiotAudioPlayer;
import io.thoughtworksarts.riot.branching.BranchingLogic;
import io.thoughtworksarts.riot.branching.JsonTranslator;
import io.thoughtworksarts.riot.facialrecognition.Emotion;
import io.thoughtworksarts.riot.facialrecognition.FacialEmotionRecognitionAPI;
import io.thoughtworksarts.riot.utilities.OSChecker;
import io.thoughtworksarts.riot.video.MediaControl;
import io.thoughtworksarts.riot.video.MoviePlayer;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@Slf4j
public class BranchingJourney extends Application {


    @Mock FacialEmotionRecognitionAPI facialEmotionRecognitionAPI;
    private MediaControl mediaControl;
    private static JsonTranslator jsonTranslator;
    private static MediaControl mediaControlSpy;

    @Test
    public static void main(String... args) throws Exception {
        log.info("Starting Riot...");
        launch(args);
//        verify(mediaControlSpy).seek(jsonTranslator.convertToDuration("04:03.010"));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initMocks(this);
        when(facialEmotionRecognitionAPI.getDominantEmotion()).thenReturn(Emotion.ANGER);

        jsonTranslator = new JsonTranslator();
        RiotAudioPlayer audioPlayer = OSChecker.isWindows() ? new AudioPlayer() : new JavaSoundAudioPlayer();
        BranchingLogic branchingLogic = new BranchingLogic(facialEmotionRecognitionAPI, jsonTranslator);
        mediaControl = new MediaControl(branchingLogic, audioPlayer, jsonTranslator.convertToDuration("10:39.200"));
        mediaControlSpy = Mockito.spy(mediaControl);
        MoviePlayer moviePlayer = new MoviePlayer(primaryStage, mediaControl);
        moviePlayer.initialise();

        mediaControl.play();
        mediaControl.seek(jsonTranslator.convertToDuration("03:47.110"));
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        mediaControl.shutdown();
    }

}
