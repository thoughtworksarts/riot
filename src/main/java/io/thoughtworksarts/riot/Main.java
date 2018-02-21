package io.thoughtworksarts.riot;

import io.thoughtworksarts.riot.audio.AudioPlayer;
import io.thoughtworksarts.riot.branching.BranchingLogic;
import io.thoughtworksarts.riot.facialrecognition.DummyFacialRecognitionAPI;
import io.thoughtworksarts.riot.video.MediaControl;
import io.thoughtworksarts.riot.video.MoviePlayer;
import javafx.application.Application;
import javafx.stage.Stage;
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
        BranchingLogic branchingLogic = new BranchingLogic();
        AudioPlayer audioPlayer = new AudioPlayer();
        DummyFacialRecognitionAPI facialRecognition = new DummyFacialRecognitionAPI();
        mediaControl = new MediaControl(branchingLogic, audioPlayer, facialRecognition);

        MoviePlayer moviePlayer = new MoviePlayer(primaryStage, mediaControl);
        moviePlayer.initialise();
        mediaControl.initialise();
        mediaControl.play();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        mediaControl.shutdown();
    }

}