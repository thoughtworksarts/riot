package io.thoughtworksarts.riot.video;

import javafx.beans.value.ObservableNumberValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import sun.security.util.Debug;

public class DebugConsole {

    private VBox debugPane;

    public DebugConsole(MediaPlayer mediaPlayer){
        this.debugPane = new VBox();
        debugPane.getChildren().addAll(
                addNumericDebugText(mediaPlayer.currentRateProperty(), "Current Rate"),
                addDurationDebugText(mediaPlayer.currentTimeProperty(), "Current Time"),
                addStatusDebugText(mediaPlayer.statusProperty(), "Status"));
    }

    public VBox getPane(){
        return this.debugPane;
    }

    private Text addNumericDebugText(ObservableNumberValue property, String label) {
        Text debugText = getDebugText();
        property.addListener(
                (observable, oldvalue, newvalue) -> {
                    debugText.setText(label + ": " + newvalue);
                }
        );
        return debugText;
    }

    private Text addDurationDebugText(ObservableValue<Duration> property, String label) {
        Text debugText = getDebugText();
        property.addListener(
                (observable, oldvalue, newvalue) -> {
                    debugText.setText(label + ": " + newvalue);
                }
        );
        return debugText;
    }

    private Text addStatusDebugText(ObservableValue<MediaPlayer.Status> property, String label){
        Text debugText = getDebugText();
        property.addListener(
                (observable, oldvalue, newvalue) -> {
                    debugText.setText(label + ": " + newvalue);
                }
        );
        return debugText;
    }

    private Text getDebugText(){
        Text debugText = new Text();
        debugText.setFont(Font.font ("Verdana", 50));
        debugText.setFill(Color.RED);
        return debugText;
    }
}
