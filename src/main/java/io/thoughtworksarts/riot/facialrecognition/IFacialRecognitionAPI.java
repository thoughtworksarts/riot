package io.thoughtworksarts.riot.facialrecognition;

public interface IFacialRecognitionAPI {
    void Initialise();
    void CaptureImage();
    float GetCalm();
    float GetFear();
    float GetAnger();


}
