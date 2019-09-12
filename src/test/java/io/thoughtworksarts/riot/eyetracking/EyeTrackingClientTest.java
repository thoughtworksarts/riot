package io.thoughtworksarts.riot.eyetracking;

import io.thoughtworksarts.riot.branching.model.Level;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class EyeTrackingClientTest {

    @Test
    public void test1() {
        EyeTrackingClient eyeTrackingClient = new EyeTrackingClient();
        eyeTrackingClient.startEyeTracking();
    }

    @Test
    public void test2() {
        EyeTrackingClient eyeTrackingClient = new EyeTrackingClient();
        eyeTrackingClient.stopEyeTracking();
    }

    @Test
    public void test3() {
        Level level = new Level();
        level.setLevel(1);
        level.setStart("hue");
        level.setEnd("endhue");
        ArrayList levels = new ArrayList();
        levels.add(level);
        EyeTrackingClient eyeTrackingClient = new EyeTrackingClient();
        eyeTrackingClient.createEyeTrackingVisualization(new ArrayList<String>(), null);
    }

}