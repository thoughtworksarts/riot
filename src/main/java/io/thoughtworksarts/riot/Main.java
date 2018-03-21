package io.thoughtworksarts.riot;

import io.thoughtworksarts.riot.audio.AudioPlayerConfigurator;
import io.thoughtworksarts.riot.audio.RiotAudioPlayer;
import io.thoughtworksarts.riot.branching.BranchingLogic;
import io.thoughtworksarts.riot.branching.JsonTranslator;
import io.thoughtworksarts.riot.facialrecognition.DeepLearningProcessor;
import io.thoughtworksarts.riot.facialrecognition.Emotion;
import io.thoughtworksarts.riot.facialrecognition.FacialEmotionRecognitionAPI;
import io.thoughtworksarts.riot.facialrecognition.ImageProcessor;
import io.thoughtworksarts.riot.video.MediaControl;
import io.thoughtworksarts.riot.video.MoviePlayer;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;

@Slf4j
public class Main extends Application {

    private MediaControl mediaControl;
    public static final String DRIVER_NAME = "ASIO4ALL v2";


    public static void main(String... args) {
        log.info("Starting Riot...");
        launch(args);
    }

    private static final Emotion[] emotionSet = {Emotion.ANGER, Emotion.FEAR, Emotion.SURPRISE, Emotion.CALM};
    private static final String emotionSetId = getEmotionSetId();
    private static final String PATH_TO_WEIGHTS_FILE = String.format("src/main/resources/facialrecognitionmodels/conv_weights_%s.h5", emotionSetId);
    private static final String PATH_TO_MODEL_FILE = String.format("src/main/resources/facialrecognitionmodels/conv_model_%s.json", emotionSetId);
    private static final String PATH_TO_EMOTION_MAP_FILE = String.format("src/main/resources/facialrecognitionmodels/conv_emotion_map_%s.json", emotionSetId);

    @Override
    public void start(Stage primaryStage) throws Exception {
        JsonTranslator jsonTranslator = new JsonTranslator();
        ImageProcessor imageProcessor = new ImageProcessor();
        DeepLearningProcessor deepLearningProcessor = new DeepLearningProcessor(PATH_TO_MODEL_FILE, PATH_TO_WEIGHTS_FILE);
        FacialEmotionRecognitionAPI facialRecognition = new FacialEmotionRecognitionAPI(imageProcessor, deepLearningProcessor, PATH_TO_EMOTION_MAP_FILE);
        BranchingLogic branchingLogic = new BranchingLogic(facialRecognition, jsonTranslator);
        RiotAudioPlayer audioPlayer = AudioPlayerConfigurator.getConfiguredRiotAudioPlayer(branchingLogic);
        mediaControl = new MediaControl(branchingLogic, audioPlayer, jsonTranslator.convertToDuration("10:39.200"));
        MoviePlayer moviePlayer = new MoviePlayer(primaryStage, mediaControl);
        moviePlayer.initialise();
        mediaControl.play();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        mediaControl.shutdown();
    }

    private static String getEmotionSetId() {
        ArrayList<Integer> emotionIds = new ArrayList<>();
        for (Emotion emotion: emotionSet) {
            emotionIds.add(emotion.getNumber());
        }
        Collections.sort(emotionIds);
        String emotionSetId = "";
        for(Integer emotionId: emotionIds) {
            emotionSetId += Integer.toString(emotionId);
        }
        return emotionSetId;
    }
}
