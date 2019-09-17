package io.thoughtworksarts.riot;

import io.thoughtworksarts.riot.branching.BranchingLogic;
import io.thoughtworksarts.riot.branching.JsonTranslator;
import io.thoughtworksarts.riot.branching.PerceptionBranchingLogic;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class Main extends Application {

    public static final String DEFAULT_FILES_PATH = "src/main/resources/facialrecognitionmodels/";
    public static final String PATH_TO_CONFIG = "src/main/resources/config.json";
    // TODO: Replace path with correct path to python calibration script
    public static final String PATH_TO_CALIBRATION_SCRIPT = "src/main/java/io/thoughtworksarts/riot/utilities/test1.py";

    public static void main(String... args) {
        log.info("Starting Riot...");
        launch(args);
    }

    @Override
    public void init() {
        log.info("Beginning calibration...");
        try {
            log.info("Attempting calibration...");
            ProcessBuilder pb = new ProcessBuilder("python", PATH_TO_CALIBRATION_SCRIPT);
            Process p = pb.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                try {
                    if (line.equals("Calibration process concluded")) {
                        log.info(line);
                    }
                } catch (Exception e) {
                    log.info("Failure during calibration.");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            log.info("Calibration failed.");
            e.printStackTrace();
        }
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
        Duration startTime = jsonTranslator.convertToDuration(jsonConfiguration.getIntros()[0].getStart());

        ImageProcessor imageProcessor = new ImageProcessor();
        DeepLearningProcessor deepLearningProcessor = new DeepLearningProcessor(pathToModelFile, pathToWeightsFile);
        FacialEmotionRecognitionAPI facialRecognition = new FacialEmotionRecognitionAPI(imageProcessor, deepLearningProcessor, pathToEmotionMapFile, jsonConfiguration.getMode());
//        BranchingLogic branchingLogic = new RiotBranchingLogic(facialRecognition, jsonTranslator,jsonConfiguration);
//        BranchingLogic branchingLogic = new PerceptionBranchingLogic(jsonTranslator, jsonConfiguration);

        MoviePlayer moviePlayer = new MoviePlayer(primaryStage, new MediaControl(startTime, filmPath, "playbacks.mp4", facialRecognition, jsonTranslator));
        moviePlayer.initialise();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
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
