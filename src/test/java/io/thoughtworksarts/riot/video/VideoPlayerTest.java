//package io.thoughtworksarts.riot.video;
//
//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;
//import javafx.stage.Stage;
//import org.junit.jupiter.api.Test;
//
//import java.io.File;
//import java.net.MalformedURLException;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class VideoPlayerTest {
//
//
//    @Test
//    void playShouldStartFilm() throws Exception {
//        //String pathToFilm = new File("src/main/resources/video/film.m4v").toURI().toURL().toString();
//        VideoPlayer videoPlayer = new VideoPlayer();
//
//        videoPlayer.start(new Stage());
//        videoPlayer.play();
//        TimeUnit.SECONDS.sleep(3);
//        videoPlayer.pause();
//        videoPlayer.shutdown();
//    }
//}