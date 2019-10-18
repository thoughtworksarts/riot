package io.thoughtworksarts.riot.video;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;


class MoviePlayerTest {
    private MoviePlayer moviePlayer;
    @Mock
    private Stage stage;
    @Mock
    private MediaControl mediaControl;
    @Mock
    private Scene scene;

    @BeforeEach
    public void setUp() {
        initMocks(this);
        doNothing().when(scene).setRoot(any());
        moviePlayer = new MoviePlayer(stage, mediaControl, scene);
        moviePlayer.initialise();
    }

    @Test
    public void shouldCallStartExperienceOnFirstSpacePress() {
        ArgumentCaptor<EventHandler<? super KeyEvent>> eventHandlerCaptor = ArgumentCaptor.forClass(EventHandler.class);

        verify(scene).addEventHandler(eq(KeyEvent.KEY_PRESSED), eventHandlerCaptor.capture());

        final EventHandler<? super KeyEvent> eventHandler = eventHandlerCaptor.getValue();
        eventHandler.handle(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.SPACE, false, false, false, false));

        verify(mediaControl).startExperience();
        verify(mediaControl, never()).startLooping();
    }


    @Test
    public void shouldCallStartLoopOnSpacePressAfterExperienceStarted() {
        ArgumentCaptor<EventHandler<? super KeyEvent>> eventHandlerCaptor = ArgumentCaptor.forClass(EventHandler.class);

        verify(scene).addEventHandler(eq(KeyEvent.KEY_PRESSED), eventHandlerCaptor.capture());

        final EventHandler<? super KeyEvent> eventHandler = eventHandlerCaptor.getValue();
        eventHandler.handle(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.SPACE, false, false, false, false));
        eventHandler.handle(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.SPACE, false, false, false, false));

        verify(mediaControl).startLooping();
    }

}