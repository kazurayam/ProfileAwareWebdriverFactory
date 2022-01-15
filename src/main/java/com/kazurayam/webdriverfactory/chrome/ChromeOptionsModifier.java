package com.kazurayam.webdriverfactory.chrome;

import org.openqa.selenium.chrome.ChromeOptions;

import com.kazurayam.webdriverfactory.chrome.ChromeOptionsModifiers.Type;

public interface ChromeOptionsModifier {

    Type getType();
    ChromeOptions modify(ChromeOptions chromeOptions);

}
