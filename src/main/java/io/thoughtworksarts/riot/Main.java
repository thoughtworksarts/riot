package io.thoughtworksarts.riot;

import io.thoughtworksarts.riot.audio.AudioPlayer;
import io.thoughtworksarts.riot.audio.JavaSoundAudioPlayer;
import io.thoughtworksarts.riot.audio.RiotAudioPlayer;
import io.thoughtworksarts.riot.branching.BranchingLogic;
import io.thoughtworksarts.riot.branching.JsonTranslator;
import io.thoughtworksarts.riot.facialrecognition.DeepLearningProcessor;
import io.thoughtworksarts.riot.facialrecognition.FacialEmotionRecognitionAPI;
import io.thoughtworksarts.riot.facialrecognition.ImageProcessor;
import io.thoughtworksarts.riot.utilities.OSChecker;
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

    private String weightsFile = "src/test/resources/conv2d_weights.h5";
    private String modelFile = "src/test/resources/conv2d_model.json";

    @Override
    public void start(Stage primaryStage) throws Exception {
        JsonTranslator jsonTranslator = new JsonTranslator();
        RiotAudioPlayer audioPlayer = OSChecker.isWindows() ? new AudioPlayer() : new JavaSoundAudioPlayer();
        ImageProcessor imageProcessor = new ImageProcessor();
        DeepLearningProcessor deepLearningProcessor = new DeepLearningProcessor(modelFile, weightsFile);
        FacialEmotionRecognitionAPI facialRecognition = new FacialEmotionRecognitionAPI(imageProcessor,deepLearningProcessor);
        BranchingLogic branchingLogic = new BranchingLogic(facialRecognition, jsonTranslator);
        mediaControl = new MediaControl(branchingLogic, audioPlayer);

        MoviePlayer moviePlayer = new MoviePlayer(primaryStage, mediaControl);
        moviePlayer.initialise();
        mediaControl.initialise();
        mediaControl.play();
        // skips to intro and jumps to level 1
        mediaControl.seek(jsonTranslator.convertToDuration("03:47.110"));
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        mediaControl.shutdown();
    }

}