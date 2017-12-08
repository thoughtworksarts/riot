package com.facialrecognition;

public interface IFacialRecognitionAPI {
    void Initialise();
    void CaptureImage();
    float GetCalm();
    float GetFear();
    float GetAnger();


}
