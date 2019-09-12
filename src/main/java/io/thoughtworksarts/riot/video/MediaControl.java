package io.thoughtworksarts.riot.video;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamImageTransformer;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.common.util.concurrent.Uninterruptibles;
import io.thoughtworksarts.riot.audio.RiotAudioPlayer;
import io.thoughtworksarts.riot.branching.BranchingLogic;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.embed.swing.SwingNode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MediaControl extends BorderPane {

    private BranchingLogic branchingLogic;
    private RiotAudioPlayer audioPlayer;
    private MediaPlayer filmPlayer;
    private final Duration audioOffset;
    private static volatile boolean webcamThreadRunning;
    private MediaPlayer playbackPlayer;
    private String playbackPath;

    private MediaView mediaView;
    private Pane pane;
    final SwingNode swingNode = new SwingNode();

    public MediaControl(BranchingLogic branchingLogic, RiotAudioPlayer audioPlayer, Duration videoStartTime, String filmPath, Duration audioOffset, String playbackPath) throws Exception {
        this.branchingLogic = branchingLogic;
        //Video relate
        String pathToFilm = new File(String.valueOf(filmPath)).toURI().toURL().toString();
        webcamThreadRunning = true;
        this.playbackPath = new File(String.valueOf(playbackPath)).toURI().toURL().toString();

        setUpFilmPlayer(pathToFilm, videoStartTime);

        setUpPlaybackPlayer(this.playbackPath);


        setUpPane(filmPlayer);
        filmPlayer.setMute(true);
//        setUpPane(playbackPlayer);
        //Audio related
        this.audioPlayer = audioPlayer;
        this.audioOffset = audioOffset;
    }


    public MediaView getMediaView()
    {
        return mediaView;
    }

    private JPanel setUpWebcamFeed(){
        JPanel window = new JPanel();

            new Thread(()->{

                while (webcamThreadRunning) {
                    try {

                        Webcam webcam = Webcam.getDefault();

                        if (!webcam.isOpen()) {
                            webcam.setViewSize(WebcamResolution.VGA.getSize());
                        }


                        WebcamPanel panel = new WebcamPanel(webcam, true);
                        webcam.setImageTransformer(new WebcamImageTransformer() {
                            @Override
                            public BufferedImage transform(BufferedImage image) {


                                    int w = image.getWidth();
                                    int h = image.getHeight();

                                    BufferedImage modified = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                                    Graphics2D g2 = modified.createGraphics();
                                    g2.drawImage(image, null, 0, 0);
                                    g2.draw(new Rectangle2D.Double(w/2, h / 2,
                                            100,
                                            100));

                                    g2.dispose();

                                    modified.flush();

                                    return modified;
                                }
                        });
                        panel.setFPSDisplayed(true);
                        panel.setDisplayDebugInfo(true);
                        panel.setImageSizeDisplayed(true);
                        panel.setMirrored(true);


                        window.add(panel);
                        window.setVisible(true);




                        Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        return window;

    }

    public static void shutDownWebcamFeed(){
        webcamThreadRunning = false;

    }


    private void setUpPane(MediaPlayer mediaPlayer) {
        mediaView = new MediaView(mediaPlayer);

        final DoubleProperty width = mediaView.fitWidthProperty();
        final DoubleProperty height = mediaView.fitHeightProperty();
        width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
        mediaView.setPreserveRatio(true);

        pane = new Pane();
        mediaView.setOnMouseClicked(event -> handleClick());

        createAndSetSwingContent(swingNode);
        pane.getChildren().addAll(mediaView, swingNode);
        pane.setStyle("-fx-background-color: black;");
        setCenter(pane);
    }

    private void setPane() {
        pane.getChildren().clear();
        mediaView.setOnMouseClicked(event -> handleClick());
        pane.getChildren().add(mediaView);
        pane.setStyle("-fx-background-color: black;");
        setCenter(pane);
    }

    private void createAndSetSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                swingNode.setContent(setUpWebcamFeed());
            }
        });
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

    public void pause() {
        log.info("Pause");
        filmPlayer.pause();
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
