package io.thoughtworksarts.riot.video;

import io.thoughtworks.riot.featuretoggle.FeatureToggle;
import io.thoughtworksarts.riot.branching.BranchingConfigurationLoader;
import io.thoughtworksarts.riot.eyetracking.EyeTrackingClient;
import io.thoughtworksarts.riot.branching.BranchingLogic;
import io.thoughtworksarts.riot.branching.JsonTranslator;
import io.thoughtworksarts.riot.branching.PerceptionBranchingLogic;
import io.thoughtworksarts.riot.branching.model.ConfigRoot;
import io.thoughtworks.riot.featuretoggle.FeatureToggle;
import io.thoughtworksarts.riot.facialrecognition.FacialEmotionRecognitionAPI;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class MediaControl extends BorderPane {

    private PerceptionBranchingLogic branchingLogic;
    private MediaPlayer filmPlayer;
    final static private String PLAYBACK_BASE_PATH = "/Users/Kiosk/riot/";
    private FeatureToggle featureToggle;

    private BranchingConfigurationLoader branchingConfigurationLoader;
    private JsonTranslator jsonTranslator;
    private FacialEmotionRecognitionAPI facialRecognition;

    private MediaView mediaView;
    private Pane pane;
    private MoviePlayer moviePlayer;
    private ConfigRoot currentConfiguration;

    private EyeTrackingClient eyeTrackingClient;

    public MediaControl(String filmPath, FacialEmotionRecognitionAPI facialRecognition, JsonTranslator jsonTranslator) {
        this.facialRecognition = facialRecognition;
        this.jsonTranslator = jsonTranslator;
        this.branchingConfigurationLoader = new BranchingConfigurationLoader(jsonTranslator);
        this.eyeTrackingClient = new EyeTrackingClient(this);

        this.mediaView = new MediaView();
        this.pane = new StackPane();
        this.featureToggle = new FeatureToggle();

        setUpFilmPlayer(new File(filmPath).toURI().toString());
        setUpMediaViewFor(filmPlayer);
        loadNextConfiguration();
    }

    private void loadNextConfiguration() {
        currentConfiguration = branchingConfigurationLoader.getNextConfiguration();
        branchingLogic = new PerceptionBranchingLogic(facialRecognition, jsonTranslator, currentConfiguration, eyeTrackingClient);
        filmPlayer.getMedia().getMarkers().clear();
        branchingLogic.recordMarkers(filmPlayer.getMedia().getMarkers());
    }

    public void startExperience() {
        filmPlayer.seek(branchingLogic.getIntro());
    }

    private boolean isPaused = false;

    private void setUpMediaViewFor(MediaPlayer mediaPlayer) {
        mediaView.setMediaPlayer(mediaPlayer);
        mediaView.setOnMouseClicked(event -> {
            if(isPaused) {
                filmPlayer.play();
                isPaused = false;
            }
            else {
                isPaused = true;
                filmPlayer.pause();
            }
        });

        final DoubleProperty width = mediaView.fitWidthProperty();
        final DoubleProperty height = mediaView.fitHeightProperty();
        width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
        mediaView.setPreserveRatio(true);

        Text debugText = new Text("0.0");
        debugText.setFont(Font.font ("Verdana", 50));
        debugText.setFill(Color.RED);
        mediaPlayer.currentRateProperty().addListener(
                (observable, oldvalue, newvalue) -> {
                    debugText.setText("Current Rate: " + newvalue);
                }
        );



        pane.getChildren().clear();
        pane.getChildren().add(mediaView);

        pane.setStyle("-fx-background-color: black;");
        setCenter(pane);
        setBottom(debugText);

    }

    private void setUpFilmPlayer(String pathToFilm) {
        Media media = new Media(pathToFilm);
        filmPlayer = new MediaPlayer(media);


        filmPlayer.setAutoPlay(false);

        filmPlayer.setOnMarker(arg -> {
            Duration duration = branchingLogic.branchOnMediaEvent(arg);
            if(duration == null && featureToggle.eyeTrackingOn()) playFirstPlayback();
            else seek(duration);
        });

        filmPlayer.setOnEndOfMedia(() -> {
            filmPlayer.getMedia().getMarkers().clear();
            loadNextConfiguration();
            seek(branchingLogic.getLoop());
            moviePlayer.activateSpacebarEventHandler();
        });
    }
    private void playFirstPlayback() {
        filmPlayer.pause();
        MediaPlayer mediaPlayer =
                new MediaPlayer(new Media(new File(PLAYBACK_BASE_PATH + currentConfiguration.getActors()[0] + "-playback.mp4").toURI().toString()));
        mediaPlayer.setOnEndOfMedia(() -> {
            playSecondPlayback();
            mediaPlayer.dispose();
        });
        setUpMediaViewFor(mediaPlayer);
        mediaPlayer.play();
        log.info("Play on 1st playback");
    }

    private void playSecondPlayback() {
        Media media = new Media(new File(PLAYBACK_BASE_PATH + currentConfiguration.getActors()[1] + "-playback.mp4").toURI().toString());
        MediaPlayer mediaPlayer =
                new MediaPlayer(media);
        mediaPlayer.setOnEndOfMedia(() -> {
            setUpMediaViewFor(filmPlayer);
            seek(this.branchingLogic.getCreditDuration());
            filmPlayer.play();
            mediaPlayer.dispose();
        });
        setUpMediaViewFor(mediaPlayer);
        mediaPlayer.play();
        log.info("Play on 2nd playback");
    }

    public void play() {
        filmPlayer.play();
    }
    public void pause(){
        filmPlayer.pause();
    }

    public void seek(Duration duration) {
        filmPlayer.seek(duration);
    }

    public void setMoviePlayer(MoviePlayer moviePlayer) {
        this.moviePlayer = moviePlayer;
    }
}
