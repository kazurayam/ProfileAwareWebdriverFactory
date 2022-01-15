package com.kazurayam.webdriverfactory.firefox;

import java.util.List;
import java.util.Map;
import com.kazurayam.webdriverfactory.firefox.FirefoxPreferencesModifiers.Type;

public interface FirefoxPreferencesModifier {

    Type getType();
    Map<String, Object> modify(Map<String, Object> preferences);
}
