package io.thoughtworksarts.riot.facialrecognition;


import org.datavec.image.loader.ImageLoader;
import org.deeplearning4j.nn.modelimport.keras.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class DeepLearningProcessor {

    private String _h5File;
    private String _jsonFile;
    private MultiLayerNetwork model;

    public DeepLearningProcessor(String jsonFile, String h5File) {
        _h5File = h5File;
        _jsonFile = jsonFile;
        this.initModel();
    }

    private void initModel() {
        try {
            model =  KerasModelImport.importKerasSequentialModelAndWeights(_jsonFile, _h5File);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKerasConfigurationException e) {
            e.printStackTrace();
        } catch (UnsupportedKerasConfigurationException e) {
            e.printStackTrace();
        }
    }

    public int GetEmotionValue(BufferedImage image, String imgFile)
    {
        ImageLoader imageLoader = new ImageLoader();

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

    public List<String> getModelLayerNames() {
        return model.getLayerNames();
    }
}
