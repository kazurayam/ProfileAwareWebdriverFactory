package com.kazurayam.browserwindowlayout

import org.openqa.selenium.Dimension
import org.openqa.selenium.Point

abstract class WindowLayoutMetrics {

    public static final WindowLayoutMetrics DEFAULT =
            new StackingWindowLayoutMetrics.Builder().disposition(new Dimension(0, 0)).build()

    abstract Point getWindowPosition(WindowLocation windowLocation)

    abstract Dimension getWindowDimension(WindowLocation windowLocation)

}
