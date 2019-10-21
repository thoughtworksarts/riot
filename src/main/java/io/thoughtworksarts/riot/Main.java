package io.thoughtworksarts.riot;

import io.thoughtworksarts.riot.branching.JsonTranslator;
import io.thoughtworksarts.riot.branching.model.ConfigRoot;
import io.thoughtworksarts.riot.facialrecognition.DeepLearningProcessor;
import io.thoughtworksarts.riot.facialrecognition.Emotion;
import io.thoughtworksarts.riot.facialrecognition.FacialEmotionRecognitionAPI;
import io.thoughtworksarts.riot.facialrecognition.ImageProcessor;
import io.thoughtworksarts.riot.video.MediaControl;
import io.thoughtworksarts.riot.video.MoviePlayer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class Main extends Application {

    private static final String DEFAULT_FILES_PATH = "/facialrecognitionmodels/";
    private static final String PATH_TO_CONFIG = "/config.json";

    public static void main(String... args) {
        log.info("Starting Riot...");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        JsonTranslator jsonTranslator = new JsonTranslator();
        ConfigRoot jsonConfiguration = jsonTranslator.populateModelsFromJson(PATH_TO_CONFIG);

        String emotionsSetId= getEmotionSetId(jsonConfiguration);
        String pathToWeightsFile = String.format( "%sconv_weights_perception.h5",DEFAULT_FILES_PATH);
        String pathToEmotionMapFile = String.format("%sconv_emotion_map_perception.json", DEFAULT_FILES_PATH, emotionsSetId);
        String filmPath = jsonConfiguration.getMedia().getVideo();

        ImageProcessor imageProcessor = new ImageProcessor();
        DeepLearningProcessor deepLearningProcessor = new DeepLearningProcessor(pathToWeightsFile);
        FacialEmotionRecognitionAPI facialRecognition = new FacialEmotionRecognitionAPI(imageProcessor, deepLearningProcessor, pathToEmotionMapFile, jsonConfiguration.getMode());

        MoviePlayer moviePlayer = new MoviePlayer(primaryStage, new MediaControl(filmPath, facialRecognition, jsonTranslator), new Scene(new Group(), 1200, 800));
        moviePlayer.initialise();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    private static String getEmotionSetId(ConfigRoot configRoot) {
        return Stream.of(configRoot.getLevels())
                .flatMap(level -> level.getBranch().keySet().stream())
                .collect(Collectors.toSet()) //Unique emotions in config.json
                .stream()
                .map(emotion -> Emotion.valueOf(emotion.toUpperCase()).getNumber())
                .sorted()
                .map(Object::toString)
                .collect(Collectors.joining());
    }
}
