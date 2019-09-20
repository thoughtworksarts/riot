package io.thoughtworksarts.riot.facialrecognition;

import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;

public class DeepLearningProcessor {

    private final String modelConfigFile;
    private MultiLayerNetwork model;

    public DeepLearningProcessor(String modelConfigFile) {
        this.modelConfigFile = modelConfigFile;
        initModel();
    }

    private void initModel() {
        try {
            model = KerasModelImport.importKerasSequentialModelAndWeights(modelConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKerasConfigurationException e) {
            e.printStackTrace();
        } catch (UnsupportedKerasConfigurationException e) {
            e.printStackTrace();
        }
    }

    public float[] getEmotionPrediction(INDArray imageData) {
        return Nd4j.toFlattened(model.output(imageData)).toFloatVector();
    }

//    public List<String> getModelLayerNames() {
//        return new ArrayList<>();//model.getLayers()[0].name;
//    }
}
