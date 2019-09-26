package io.thoughtworks.riot.featuretoggle;

public class FeatureToggle {

    boolean eyeTrackingOn = false;
    boolean loggingOn = true;

    public boolean eyeTrackingOn()
    {
        return eyeTrackingOn;
    }
    public boolean loggingOn() { return loggingOn; }

}
