package io.thoughtworksarts.riot.video;

import io.thoughtworksarts.riot.audio.AudioPlayer;
import io.thoughtworksarts.riot.branching.BranchingLogic;
import io.thoughtworksarts.riot.branching.ConfigRoot;
import io.thoughtworksarts.riot.branching.EmotionBranch;
import io.thoughtworksarts.riot.branching.Level;
import io.thoughtworksarts.riot.facialrecognition.DummyFacialRecognitionAPI;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class MediaControl extends BorderPane {

    public static final String PATH_TO_CONFIG = "src/main/resources/config.json";
    public static final String DRIVER_NAME = "ASIO4ALL v2";

    private BranchingLogic branchingLogic;
    private AudioPlayer audioPlayer;
    private DummyFacialRecognitionAPI facialRecognition;

    private MediaView mediaView;
    private MediaPlayer filmPlayer;
    private Pane pane;
    private Media media;
    private ConfigRoot root;

    public MediaControl(BranchingLogic branchingLogic, AudioPlayer audioPlayer, DummyFacialRecognitionAPI facialRecognition) throws Exception {
        this.branchingLogic = branchingLogic;
        this.audioPlayer = audioPlayer;
        this.facialRecognition = facialRecognition;

        root = branchingLogic.createLogicTree(PATH_TO_CONFIG);
        String pathToFilm = new File(String.valueOf(root.getVideo())).toURI().toURL().toString();

        media = new Media(pathToFilm);
        this.filmPlayer = new MediaPlayer(media);
        this.mediaView = new MediaView(filmPlayer);
        this.pane = new Pane();

    }

    public void initialise() throws Exception {
        branchingLogic.recordMarkers(media.getMarkers());
//        audioPlayer.initialise(DRIVER_NAME, root.getAudio());
        facialRecognition.initialise();

        setUpPane();
        setUpFilmPlayer();
    }

    private void setUpPane() {
        pane.getChildren().add(mediaView);
        pane.setStyle("-fx-background-color: black;");
        setCenter(pane);
    }

    private void setUpFilmPlayer() {

        filmPlayer.setAutoPlay(false);
        filmPlayer.setOnMarker(arg -> {
            String key = arg.getMarker().getKey();
            String[] split = key.split(":");
            String category = split[0];
            int index = Integer.parseInt(split[1]);
            Level level = root.getLevels()[index - 1];

            if( category.equals("level") ) {
                log.info("Level Marker: " + key);
                String value = facialRecognition.getDominateEmotion().name();
                EmotionBranch emotionBranch = level.getBranch().get(value.toLowerCase());
                seek(emotionBranch.getStart());
            } else if( category.equals("emotion")) {
                log.info("Emotion Marker: " + key);
                String emotionType = split[2];
                EmotionBranch emotionBranch = level.getBranch().get(emotionType);
                int outcomeNumber = emotionBranch.getOutcome();
                if(outcomeNumber == 0 ) {
                    pause();
                } else {
                    Level nextLevel = root.getLevels()[outcomeNumber - 1];
                    String start = nextLevel.getStart();
                    seek(start);
                }
            }
        });
    }

    public void pause() {
        audioPlayer.pause();
        filmPlayer.pause();
        log.info("Paused");
    }

    public void play() {
        audioPlayer.resume();
        filmPlayer.play();
    }

    public void seek(String time) {
        log.info("Seeking: " + time);
        Duration duration = branchingLogic.stringToDuration(time);
        audioPlayer.seek(duration.toSeconds());
        filmPlayer.seek(duration);
    }

    public void shutdown() {
        audioPlayer.shutdown();
        filmPlayer.stop();
    }
}