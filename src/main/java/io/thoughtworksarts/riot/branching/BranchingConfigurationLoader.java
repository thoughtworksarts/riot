package io.thoughtworksarts.riot.branching;

import io.thoughtworksarts.riot.branching.model.ConfigRoot;
import io.thoughtworksarts.riot.logger.PerceptionLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class BranchingConfigurationLoader {
    public static final String PATH_TO_CONFIG = "src/main/resources/";
    private List<String> paths;
    private JsonTranslator jsonTranslator;
    private PerceptionLogger logger;
    int currentConfigurationIndex = 0;

    public BranchingConfigurationLoader(JsonTranslator jsonTranslator) {
        this.jsonTranslator = jsonTranslator;
        this.logger = new PerceptionLogger("BranchingConfigurationLoader");
        paths = new ArrayList<>();
        paths.add("config-badbw.json");
        paths.add("config-badwb.json");
        paths.add("config-goodbw.json");
        paths.add("config-goodwb.json");
        Collections.shuffle(paths);
    }

    public ConfigRoot getNextConfiguration() {
        if(currentConfigurationIndex == paths.size()) {
            currentConfigurationIndex = 0;
            Collections.shuffle(paths);
        }
        try {
            return jsonTranslator.populateModelsFromJson(PATH_TO_CONFIG + paths.get(currentConfigurationIndex++));
        } catch (Exception e) {
            logger.log(Level.INFO, "getNextConfiguration", e.getMessage(), null);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


}
