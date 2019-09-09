package io.thoughtworksarts.riot;

import io.thoughtworksarts.riot.audio.AudioPlayerConfigurator;
import io.thoughtworksarts.riot.audio.RiotAudioPlayer;
import io.thoughtworksarts.riot.branching.BranchingLogic;
import io.thoughtworksarts.riot.branching.JsonTranslator;
import io.thoughtworksarts.riot.branching.model.ConfigRoot;
import io.thoughtworksarts.riot.facialrecognition.DeepLearningProcessor;
import io.thoughtworksarts.riot.facialrecognition.Emotion;
import io.thoughtworksarts.riot.facialrecognition.FacialEmotionRecognitionAPI;
import io.thoughtworksarts.riot.facialrecognition.ImageProcessor;
import io.thoughtworksarts.riot.video.MediaControl;
import io.thoughtworksarts.riot.video.MoviePlayer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class Main extends Application {

    public static final String DEFAULT_FILES_PATH = "src/main/resources/facialrecognitionmodels/";
    public static final String PATH_TO_CONFIG = "src/main/resources/config.json";

    private MediaControl mediaControl;

    public static void main(String... args) {
        log.info("Starting Riot...");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        JsonTranslator jsonTranslator = new JsonTranslator();
        ConfigRoot jsonConfiguration = jsonTranslator.populateModelsFromJson(PATH_TO_CONFIG);

        String emotionsSetId= getEmotionSetId(jsonConfiguration);
        String pathToWeightsFile = String.format( "%sconv_weights_%s.h5",DEFAULT_FILES_PATH, emotionsSetId);
        String pathToModelFile = String.format("%sconv_model_%s.json",DEFAULT_FILES_PATH, emotionsSetId);
        String pathToEmotionMapFile = String.format("%sconv_emotion_map_%s.json", DEFAULT_FILES_PATH, emotionsSetId);
        String filmPath = jsonConfiguration.getMedia().getVideo();
        String audioPath = jsonConfiguration.getMedia().getAudio();
        Duration startTime = jsonTranslator.convertToDuration(jsonConfiguration.getIntros()[0].getStart());

        ImageProcessor imageProcessor = new ImageProcessor();
        DeepLearningProcessor deepLearningProcessor = new DeepLearningProcessor(pathToModelFile, pathToWeightsFile);
        FacialEmotionRecognitionAPI facialRecognition = new FacialEmotionRecognitionAPI(imageProcessor, deepLearningProcessor, pathToEmotionMapFile, jsonConfiguration.getMode());
        BranchingLogic branchingLogic = new BranchingLogic(facialRecognition, jsonTranslator,jsonConfiguration);
        RiotAudioPlayer audioPlayer = AudioPlayerConfigurator.getConfiguredRiotAudioPlayer(audioPath);
        mediaControl = new MediaControl(branchingLogic, audioPlayer,startTime ,filmPath, jsonTranslator.convertToDuration("00:00.000"));
        MoviePlayer moviePlayer = new MoviePlayer(primaryStage, mediaControl);
        moviePlayer.initialise();
        mediaControl.play();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        mediaControl.shutdown();
    }

    private static String getEmotionSetId(ConfigRoot configRoot) {
        return Stream.of(configRoot.getLevels())
                .flatMap(level -> level.getBranch().entrySet().stream()
                        .map(Map.Entry::getKey))
                .collect(Collectors.toSet()) //Unique emotions in config.json
                .stream()
                .map(emotion -> Emotion.valueOf(emotion.toUpperCase()).getNumber())
                .sorted()
                .map(number -> number.toString())
                .collect(Collectors.joining());
    }
}
