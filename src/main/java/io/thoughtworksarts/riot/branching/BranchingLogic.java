package io.thoughtworksarts.riot.branching;

import javafx.scene.media.MediaMarkerEvent;
import javafx.util.Duration;

import java.util.Map;

public interface BranchingLogic {

    Duration branchOnMediaEvent(MediaMarkerEvent arg);

    void recordMarkers(Map<String, Duration> markers);

    Duration getClickSeekTime(Duration currentTime);

    Duration getCreditDuration();

    Duration getIntro();

    Duration getLoop();


}
