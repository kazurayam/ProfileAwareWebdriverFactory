package com.kazurayam.webdriverfactory.edge;

import java.util.Map;
import com.kazurayam.webdriverfactory.edge.EdgePreferencesModifiers.Type;

public interface EdgePreferencesModifier {
    Type getType();
    Map<String, Object> modify(Map<String, Object> preferences);

}
