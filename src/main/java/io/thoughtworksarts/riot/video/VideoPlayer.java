package io.thoughtworksarts.riot.video;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.File;

public class VideoPlayer extends Application{


    private Media media;
    private MediaPlayer mediaPlayer;

    public void play(){
        mediaPlayer.play();
    }

    public void pause(){
        mediaPlayer.pause();
    }

    public void shutdown(){
        mediaPlayer.stop();
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Create and set the Scene.
        Scene scene = new Scene(new Group(), 540, 209);
        stage.setScene(scene);

        // Name and display the Stage.
        stage.setTitle("Hello Media");
        stage.show();

        // Create the media source.
        String pathToFilm = new File("src/main/resources/video/film.m4v").toURI().toURL().toString();
        media = new Media(pathToFilm);

        // Create the player and set to play automatically.
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);

        // Create the view and add it to the Scene.
        MediaView mediaView = new MediaView(mediaPlayer);
        ((Group) scene.getRoot()).getChildren().add(mediaView);

    }
}
