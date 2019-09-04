package io.thoughtworksarts.riot.functional;

import io.thoughtworksarts.riot.audio.AudioPlayerConfigurator;
import io.thoughtworksarts.riot.audio.RiotAudioPlayer;
import io.thoughtworksarts.riot.branching.BranchingLogic;
import io.thoughtworksarts.riot.branching.JsonTranslator;
import io.thoughtworksarts.riot.branching.model.ConfigRoot;
import io.thoughtworksarts.riot.branching.model.Level;
import io.thoughtworksarts.riot.facialrecognition.Emotion;
import io.thoughtworksarts.riot.facialrecognition.FacialEmotionRecognitionAPI;
import io.thoughtworksarts.riot.video.MediaControl;
import io.thoughtworksarts.riot.video.MoviePlayer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FearJourneyTest extends Application {

    public static final String PATH_TO_CONFIG = "src/main/resources/config.json";


    @Mock FacialEmotionRecognitionAPI facialEmotionRecognitionAPI;
    private MediaControl mediaControl;
    private static JsonTranslator jsonTranslator;

    @Test
    public static void main(String... args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initMocks(this);
        when(facialEmotionRecognitionAPI.getDominantEmotion()).thenReturn(Emotion.FEAR);

        jsonTranslator = new JsonTranslator();
        ConfigRoot root = jsonTranslator.populateModelsFromJson(PATH_TO_CONFIG);
        Level[] levels = root.getLevels();
        String videoPath = root.getMedia().getVideo();

        //Play the movie
        RiotAudioPlayer audioPlayer = AudioPlayerConfigurator.getConfiguredRiotAudioPlayer(root.getMedia().getAudio());
        BranchingLogic branchingLogic = new BranchingLogic(facialEmotionRecognitionAPI, jsonTranslator,root);
        mediaControl = new MediaControl(branchingLogic, audioPlayer, jsonTranslator.convertToDuration("04:00.000"),videoPath, jsonTranslator.convertToDuration("00:00.000"));

//        mediaControlSpy = Mockito.spy(mediaControl);
        MoviePlayer moviePlayer = new MoviePlayer(primaryStage, mediaControl);
        moviePlayer.initialise();
        mediaControl.play();

        //Skip to end of level1
        String endLevel1 = levels[0].getEnd();
        Duration nearEndOfLevel = subtractTimeFromDuration("00:03.000", endLevel1);
        mediaControl.seek(nearEndOfLevel);

        //Verify that end of level1 goes to fear scene

        //Skip the middle of fear of scene
        String startFearScene = levels[0].getBranch().get("fear").getStart();
        Duration nearStartOfFearScene = addTimeToDuration("00:03.00", startFearScene);


        String endFearScene = levels[0].getBranch().get("fear").getEnd();
        Duration nearEndOfFearScene = subtractTimeFromDuration("00:03.000", endFearScene);
        mediaControl.seek(nearEndOfFearScene);

    }

    //Strings must be in 00:00.000
    public Duration subtractTimeFromDuration(String seconds, String time){
        Duration timeInDuration = jsonTranslator.convertToDuration(time);
        Duration secondsInDuration = jsonTranslator.convertToDuration(seconds);
        return timeInDuration.subtract(secondsInDuration);
    }

    //Strings must be in 00:00.000
    public Duration addTimeToDuration(String seconds, String time){
        Duration timeInDuration = jsonTranslator.convertToDuration(time);
        Duration secondsInDuration = jsonTranslator.convertToDuration(seconds);
        return timeInDuration.add(secondsInDuration);
    }


    @Override
    public void stop() throws Exception {
        super.stop();
        mediaControl.shutdown();
    }

}
