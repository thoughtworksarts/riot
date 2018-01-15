package io.thoughtworksarts.riot.facialrecognition;

import com.github.sarxos.webcam.Webcam;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FacialRecognitionV2API implements IFacialRecognitionAPI  {



    public void Initialise() {

    }

    public void CaptureImage() {
        // get default webcam and open it
        Webcam webcam = Webcam.getDefault();
        webcam.open();

        // get image
        BufferedImage image = webcam.getImage();

        // save image to PNG file
        try {
            ImageIO.write(image, "PNG", new File("testimage.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public float GetCalm() {
        return 0;
    }

    public float GetFear() {
        return 0;
    }

    public float GetAnger() {
        return 0;
    }

}
