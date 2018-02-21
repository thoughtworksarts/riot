package io.thoughtworksarts.riot;

import io.thoughtworksarts.riot.audio.AudioPlayer;
import io.thoughtworksarts.riot.branching.BranchingLogic;
import io.thoughtworksarts.riot.facialrecognition.DummyFacialRecognitionAPI;
import io.thoughtworksarts.riot.video.MoviePlayer;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main extends Application {

    private RiotController riotController;

    public static void main(String... args) {
        log.info("Starting Riot...");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        MoviePlayer moviePlayer = new MoviePlayer(primaryStage);
        AudioPlayer audioPlayer = new AudioPlayer();
        BranchingLogic branchingLogic = new BranchingLogic();
        DummyFacialRecognitionAPI facialRecognition = new DummyFacialRecognitionAPI();

        riotController = new RiotController(audioPlayer, moviePlayer, branchingLogic, facialRecognition);
        riotController.initRiot();
        riotController.runRiot();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        riotController.endRiot();
    }

}