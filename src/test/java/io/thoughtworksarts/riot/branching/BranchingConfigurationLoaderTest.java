package io.thoughtworksarts.riot.branching;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BranchingConfigurationLoaderTest {
    BranchingConfigurationLoader branchingConfigurationLoader;

    @BeforeEach
    private void setUp() {
        branchingConfigurationLoader = new BranchingConfigurationLoader(new JsonTranslator());
    }

    @Test
    public void hue() {
        branchingConfigurationLoader.getNextConfiguration();
        branchingConfigurationLoader.getNextConfiguration();
        branchingConfigurationLoader.getNextConfiguration();
        branchingConfigurationLoader.getNextConfiguration();
        branchingConfigurationLoader.getNextConfiguration();
        branchingConfigurationLoader.getNextConfiguration();
        branchingConfigurationLoader.getNextConfiguration();
        branchingConfigurationLoader.getNextConfiguration();
        branchingConfigurationLoader.getNextConfiguration();
        branchingConfigurationLoader.getNextConfiguration();
        branchingConfigurationLoader.getNextConfiguration();
        branchingConfigurationLoader.getNextConfiguration();
    }

}