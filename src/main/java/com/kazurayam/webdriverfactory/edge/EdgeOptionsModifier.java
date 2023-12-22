package com.kazurayam.webdriverfactory.edge;

import com.kazurayam.webdriverfactory.edge.EdgeOptionsModifiers.Type;
import org.openqa.selenium.edge.EdgeOptions;

public interface EdgeOptionsModifier {
    Type getType();
    EdgeOptions modify(EdgeOptions edgeOptions);

}
