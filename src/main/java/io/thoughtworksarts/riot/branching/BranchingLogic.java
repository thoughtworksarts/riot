package io.thoughtworksarts.riot.branching;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;

@Slf4j
public class BranchingLogic {

    @Getter private ConfigRoot root;

    public ConfigRoot createLogicTree(String pathToConfig) throws Exception {
        String jsonConfig = readFile(pathToConfig);
        ObjectMapper objectMapper = new ObjectMapper();
        root = objectMapper.readValue(jsonConfig, ConfigRoot.class);
        return root;
    }

    public Level getOutcome(Emotion currentEmotion) {
        int outcome = currentEmotion.getOutcome();
        if ( outcome == 0 ) return null;
        return root.getLevels()[outcome - 1];
    }

    private String readFile(String filename) {
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            result = sb.toString();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
