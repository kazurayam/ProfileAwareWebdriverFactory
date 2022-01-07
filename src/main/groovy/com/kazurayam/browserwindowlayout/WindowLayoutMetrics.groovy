package com.kazurayam.browserwindowlayout

import org.openqa.selenium.Dimension
import org.openqa.selenium.Point

abstract class WindowLayoutMetrics {

    abstract int getSize()

    abstract Point getWindowPosition(int windowIndex)

    abstract Dimension getWindowDimension(int windowIndex)

}
