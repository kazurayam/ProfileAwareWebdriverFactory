package com.kazurayam.webdriverfactory.firefox;

import org.openqa.selenium.firefox.FirefoxOptions;

import com.kazurayam.webdriverfactory.firefox.FirefoxOptionsModifiers.Type;

public interface FirefoxOptionsModifier {

    Type getType();

    FirefoxOptions modify(FirefoxOptions firefoxOptions);

}
