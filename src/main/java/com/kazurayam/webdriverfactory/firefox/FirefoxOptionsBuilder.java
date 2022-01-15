package com.kazurayam.webdriverfactory.firefox;

import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.Map;

public interface FirefoxOptionsBuilder {
    public static FirefoxOptionsBuilder newInstance() {
        return new FirefoxOptionsBuilderImpl();
    }

    public static FirefoxOptionsBuilder newInstance(Map<String, Object> preferences) {
        return new FirefoxOptionsBuilderImpl(preferences);
    }

    public abstract FirefoxOptions build();

}
