package com.kazurayam.browserwindowlayout

import org.openqa.selenium.Dimension
import org.openqa.selenium.Point

class StackingWindowLayoutMetrics extends WindowLayoutMetrics {

    public static final StackingWindowLayoutMetrics DEFAULT =
            new StackingWindowLayoutMetrics.Builder().build()

    private final Dimension windowDimension
    private final Dimension disposition

    Dimension getDisposition() {
        return disposition
    }

    @Override
    Point getWindowPosition(WindowLocation windowLocation) {
        int x = disposition.width * windowLocation.index
        int y = disposition.height * windowLocation.index
        return new Point(x, y)
    }

    @Override
    Dimension getWindowDimension(WindowLocation windowLocation) {
        return windowDimension
    }

    @Override
    boolean equals(Object o) {
        if (o.is(this)) {
            return true
        }
        if (!(o instanceof StackingWindowLayoutMetrics)) {
            return false
        }
        StackingWindowLayoutMetrics other = (StackingWindowLayoutMetrics)o
        return this.windowDimension == other.windowDimension &&
                this.disposition == other.disposition
    }

    @Override
    int hashCode() {
        int result = 17
        result = 31 * result + this.windowDimension.hashCode()
        result = 31 * result + this.disposition.hashCode()
        return result
    }

    @Override
    String toString() {
        StringBuilder sb = new StringBuilder()
        sb.append("{\"StackingWindowLayoutMetrics\":{")
        sb.append("\"windowDimension\":[${windowDimension.width},${windowDimension.height}]")
        sb.append(",")
        sb.append("\"disposition\":[${disposition.width},${disposition.height}]")
        sb.append("}}")
        return sb.toString()
    }

    /**
     * Builder by Effective Java
     */
    static class Builder {
        // Required parameters

        // Optional parameters - initialized to default values
        private Dimension windowDimension = new Dimension(1280, 600)
        private Dimension disposition = new Dimension(80, 80)

        Builder() {
        }

        Builder windowDimension(Dimension windowDimension) {
            this.windowDimension = windowDimension
            return this
        }

        Builder disposition(Dimension disposition) {
            this.disposition = disposition
            return this
        }

        StackingWindowLayoutMetrics build() {
            return new StackingWindowLayoutMetrics(this)
        }
    }


    private StackingWindowLayoutMetrics(Builder builder) {
        windowDimension = builder.windowDimension
        disposition     = builder.disposition
    }


}
