package io.thoughtworksarts.riot;

import io.thoughtworksarts.riot.audio.AudioPlayer;
import io.thoughtworksarts.riot.audio.JavaSoundAudioPlayer;
import io.thoughtworksarts.riot.audio.RiotAudioPlayer;
import io.thoughtworksarts.riot.branching.BranchingLogic;
import io.thoughtworksarts.riot.branching.JsonTranslator;
import io.thoughtworksarts.riot.facialrecognition.DummyFacialRecognitionAPI;
import io.thoughtworksarts.riot.video.MediaControl;
import io.thoughtworksarts.riot.video.MoviePlayer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main extends Application {

    private MediaControl mediaControl;

    public static void main(String... args) {
        log.info("Starting Riot...");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        JsonTranslator jsonTranslator = new JsonTranslator();
        RiotAudioPlayer audioPlayer = OSChecker.isWindows() ? new AudioPlayer() : new JavaSoundAudioPlayer();
        DummyFacialRecognitionAPI facialRecognition = new DummyFacialRecognitionAPI("src/test/resources/neuralNetConfig.json");
        BranchingLogic branchingLogic = new BranchingLogic(facialRecognition, jsonTranslator);
        mediaControl = new MediaControl(branchingLogic, audioPlayer);

        MoviePlayer moviePlayer = new MoviePlayer(primaryStage, mediaControl);
        moviePlayer.initialise();
        mediaControl.initialise();
        mediaControl.play();
        mediaControl.seek(new Duration(123000.0));
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        mediaControl.shutdown();
    }

}