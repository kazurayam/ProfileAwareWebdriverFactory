package com.kazurayam.webdriverfactory.edge;

import org.openqa.selenium.edge.EdgeOptions;

import java.util.HashMap;
import java.util.Map;

public class EdgeOptionsBuilderImpl extends EdgeOptionsBuilder {

    public EdgeOptionsBuilderImpl() {
        this(new HashMap<String, Object>());
    }

    public EdgeOptionsBuilderImpl(Map<String, Object> preferences) {
        this.preferences = preferences;
    }

    @Override
    public EdgeOptions build() {
        EdgeOptions options = new EdgeOptions();
        //options.setExperimentalOption("prefs", this.preferences);
        return options;
    }

    private final Map<String, Object> preferences;

}
