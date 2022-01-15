package com.kazurayam.webdriverfactory.chrome;

import java.util.Map;
import com.kazurayam.webdriverfactory.chrome.ChromePreferencesModifiers.Type;

public interface ChromePreferencesModifier {

    Type getType();
    Map<String, Object> modify(Map<String, Object> preferences);
}
