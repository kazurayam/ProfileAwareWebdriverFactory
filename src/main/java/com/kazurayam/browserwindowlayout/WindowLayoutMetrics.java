package com.kazurayam.browserwindowlayout;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

public abstract class WindowLayoutMetrics {
    public abstract int getSize();

    public abstract Point getWindowPosition(int windowIndex);

    public abstract Dimension getWindowDimension(int windowIndex);
}
