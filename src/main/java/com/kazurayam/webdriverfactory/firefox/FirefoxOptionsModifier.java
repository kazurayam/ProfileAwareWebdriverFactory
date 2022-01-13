package com.kazurayam.webdriverfactory.firefox;

import org.openqa.selenium.firefox.FirefoxOptions;

public interface FirefoxOptionsModifier {
    public abstract Type getType();

    public abstract FirefoxOptions modify(FirefoxOptions firefoxOptions);

    public static enum Type {
        headless, windowSize, windowSize1024x768, withProfile, withProfileDirectoryName;
    }
}
