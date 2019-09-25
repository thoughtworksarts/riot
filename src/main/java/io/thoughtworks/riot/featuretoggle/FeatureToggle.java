package io.thoughtworks.riot.featuretoggle;

public class FeatureToggle {

    boolean eyeTrackingOn = false;
    boolean debugConsoleOn = true;

    public boolean eyeTrackingOn()
    {
        return eyeTrackingOn;
    }

    public boolean debugConsoleOn() { return debugConsoleOn; }


}
