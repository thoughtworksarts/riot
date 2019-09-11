package io.thoughtworksarts.riot.video;

import io.thoughtworksarts.riot.audio.RiotAudioPlayer;
import io.thoughtworksarts.riot.branching.BranchingLogic;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
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

    private BranchingLogic branchingLogic;
    private RiotAudioPlayer audioPlayer;
    private MediaPlayer filmPlayer;
    private MediaPlayer playbackPlayer;
    private String playbackPath;
    //private final Duration audioOffset;
    private MediaView mediaView;
    private Pane pane;

    public MediaControl(BranchingLogic branchingLogic, RiotAudioPlayer audioPlayer, Duration videoStartTime, String filmPath, Duration audioOffset, String playbackPath) throws Exception {
        this.branchingLogic = branchingLogic;
        //Video relate
        String pathToFilm = new File(String.valueOf(filmPath)).toURI().toURL().toString();
        this.playbackPath = new File(String.valueOf(playbackPath)).toURI().toURL().toString();

        setUpFilmPlayer(pathToFilm, videoStartTime);
        setUpPlaybackPlayer(this.playbackPath);


        setUpPane(filmPlayer);
//        setUpPane(playbackPlayer);
        //Audio related
        //this.audioPlayer = audioPlayer;
        //this.audioOffset = audioOffset;
    }


    public MediaView getMediaView()
    {
        return mediaView;
    }


    private void setUpPane(MediaPlayer mediaPlayer) {
        mediaView = new MediaView(mediaPlayer);

        final DoubleProperty width = mediaView.fitWidthProperty();
        final DoubleProperty height = mediaView.fitHeightProperty();
        width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
        mediaView.setPreserveRatio(true);

        pane = new Pane();
        setPane();
    }

    private void setPane() {
        pane.getChildren().clear();
        mediaView.setOnMouseClicked(event -> handleClick());
        pane.getChildren().add(mediaView);
        pane.setStyle("-fx-background-color: black;");
        setCenter(pane);
    }

    private void setUpFilmPlayer(String pathToFilm, Duration startTime) {
        Media media = new Media(pathToFilm);
        branchingLogic.recordMarkers(media.getMarkers());
        filmPlayer = new MediaPlayer(media);

        filmPlayer.setAutoPlay(false);
        filmPlayer.setOnMarker(arg -> {
            Duration duration = branchingLogic.branchOnMediaEvent(arg);

            if(duration == null)
            {
                filmPlayer.stop();

                mediaView.setMediaPlayer(playbackPlayer);
                setPane();
                playbackPlayer.play();
            }
            else {
                seek(duration);
            }
        });

        filmPlayer.setOnReady(() -> {
                    filmPlayer.seek(startTime);
                    //audioPlayer.seek(startTime.toSeconds());
                }
        );
    }

    private void setUpPlaybackPlayer(String pathToFilm) {

        Media media = new Media(pathToFilm);
        playbackPlayer = new MediaPlayer(media);
        //media
        playbackPlayer.setOnEndOfMedia(() -> {
            playbackPlayer.stop();
            filmPlayer.setStartTime(this.branchingLogic.getCreditDuration());

            mediaView.setMediaPlayer(filmPlayer);

            setPane();
            filmPlayer.play();
        });
    }

    private void handleClick() {

        Duration duration = branchingLogic.getClickSeekTime(filmPlayer.getCurrentTime());
        if (duration != null) {
            seek(duration);
        } else {
            log.info("Clicking is not allowed at this particular time point.");
        }
    }

    public void play() {
        log.info("Play");
//        playbackPlayer.play();
        filmPlayer.play();
        //audioPlayer.resume();
    }

    public void seek(Duration duration) {
        filmPlayer.seek(duration);
        //audioPlayer.seek(duration.add(audioOffset).toSeconds());
        //audioPlayer.resume();  // this needs to be here because the audioPlayer stops after seeking sometimes

    }

    public void shutdown() {
        log.info("Shutting Down");
        filmPlayer.stop();
        //audioPlayer.shutdown();
    }
}
