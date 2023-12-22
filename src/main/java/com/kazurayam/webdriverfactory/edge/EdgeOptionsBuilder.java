package com.kazurayam.webdriverfactory.edge;

import org.openqa.selenium.edge.EdgeOptions;

import java.util.Map;

public abstract class EdgeOptionsBuilder {

    public static EdgeOptionsBuilder newInstance() {
        return new EdgeOptionsBuilderImpl();
    }

    public static EdgeOptionsBuilder newInstance(Map<String, Object> chromePreferences) {
        return new EdgeOptionsBuilderImpl(chromePreferences);
    }

    public abstract EdgeOptions build();

}
