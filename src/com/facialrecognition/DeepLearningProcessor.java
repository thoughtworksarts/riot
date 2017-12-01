package com.facialrecognition;


import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.modelimport.keras.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import org.deeplearning4j.util.ImageLoader;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DeepLearningProcessor {

    private String _h5File;
    private String _jsonFile;
    public DeepLearningProcessor(String h5File, String jsonFile)
    {
        _h5File = h5File;
        _jsonFile = jsonFile;
    }

    public int GetEmotionValue(BufferedImage image, String imgFile)
    {
        MultiLayerNetwork model = null;
        ImageLoader imageLoader = new ImageLoader();

        try {
            model =  KerasModelImport.importKerasSequentialModelAndWeights(_jsonFile, _h5File);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKerasConfigurationException e) {
            e.printStackTrace();
        } catch (UnsupportedKerasConfigurationException e) {
            e.printStackTrace();
        }
        //Evaluation evaluation = new Evaluation(3);
        INDArray output = null;
        try {
            output = model.output(imageLoader.asMatrix( new File(imgFile)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        int[] predictions = model.predict(output);
        return predictions[0];

    }
}
