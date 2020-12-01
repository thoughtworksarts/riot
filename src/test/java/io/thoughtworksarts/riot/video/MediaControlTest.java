package io.thoughtworksarts.riot.video;

import io.thoughtworksarts.riot.branching.JsonTranslator;
import io.thoughtworksarts.riot.branching.PerceptionBranchingLogic;
import io.thoughtworksarts.riot.facialrecognition.FacialEmotionRecognitionAPI;
import javafx.embed.swing.JFXPanel;
import javafx.util.Duration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.mockito.Mock;

import javax.swing.*;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.MockitoAnnotations.initMocks;

class MediaControlTest {
    @Mock
    private FacialEmotionRecognitionAPI facialRecognition;
    private JsonTranslator jsonTranslator = new JsonTranslator();

    @BeforeAll
    public static void initToolkit()
            throws InterruptedException
    {
        // https://stackoverflow.com/a/28501560
        final CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            new JFXPanel(); // initializes JavaFX environment
            latch.countDown();
        });

        if (!latch.await(5L, TimeUnit.SECONDS))
            throw new ExceptionInInitializerError();
    }

    @BeforeEach
    public void setUp() {
        initMocks(this);
    }

    @Disabled @Test
    public void shouldResetAppWhenGoingBackToInitialLoop() throws URISyntaxException {
        final MediaControl mediaControl = new MediaControl("/video/final-film.m4v", facialRecognition, jsonTranslator);

        mediaControl.startExperience();
        final PerceptionBranchingLogic initialBranchingLogic = mediaControl.getBranchingLogic();
        final Map<String, Duration> initialMarkers = mediaControl.getFilmPlayer().getMedia().getMarkers();

        mediaControl.startLooping();
        final PerceptionBranchingLogic newBranchingLogic = mediaControl.getBranchingLogic();
        final Map<String, Duration> newMarkers = mediaControl.getFilmPlayer().getMedia().getMarkers();

        assertNotEquals(initialBranchingLogic, newBranchingLogic);
    }
}