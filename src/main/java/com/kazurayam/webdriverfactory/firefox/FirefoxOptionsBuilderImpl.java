package com.kazurayam.webdriverfactory.firefox;

import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.Map;

public class FirefoxOptionsBuilderImpl implements FirefoxOptionsBuilder {

    private Map<String, Object> preferences;

    public FirefoxOptionsBuilderImpl() {
    }

    public FirefoxOptionsBuilderImpl(Map<String, Object> preferences) {
        this.preferences = preferences;
    }

    /**
     *
     */
    @Override
    public FirefoxOptions build() {
        FirefoxOptions options = new FirefoxOptions();

        // set location of the Chrome Browser's binary
        options.setBinary(FirefoxDriverUtils.getFirefoxBinaryPath().toString());

        // set my preferences
        preferences.forEach((k,v) -> {
            // FIXME: this line doesn't compile.
            // I dont know why.
            //options.addPreference(k,v);
        });

        // The following lines are copy&pasted from
        // https://github.com/SeleniumHQ/selenium/issues/4961

        return options;
    }

}
