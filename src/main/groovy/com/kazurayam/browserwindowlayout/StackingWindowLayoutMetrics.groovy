package com.kazurayam.browserwindowlayout

import org.openqa.selenium.Dimension
import org.openqa.selenium.Point

class StackingWindowLayoutMetrics extends WindowLayoutMetrics {

    private final int size
    private final Dimension windowDimension
    private final Point disposition

    private StackingWindowLayoutMetrics(Builder builder) {
        size            = builder.size
        windowDimension = builder.windowDimension
        disposition     = builder.disposition
    }

    Dimension getWindowDimension() {
        return this.windowDimension
    }

    Point getDisposition() {
        return this.disposition
    }

    @Override
    int getSize() {
        return this.size
    }

    @Override
    Point getWindowPosition(int windowIndex) {
        if (windowIndex < 0 || windowIndex >= this.size) {
            throw new IllegalArgumentException("windowIndex must be >=0 and <${this.size}")
        }
        int x = disposition.x * windowIndex
        int y = disposition.y * windowIndex
        return new Point(x, y)
    }

    @Override
    Dimension getWindowDimension(int windowIndex) {
        return this.windowDimension
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
        sb.append("\"size\":${this.size}")
        sb.append(",")
        sb.append("\"windowDimension\":{\"width\":${windowDimension.width},\"height\":${windowDimension.height}}")
        sb.append(",")
        sb.append("\"disposition\":{\"x\":${disposition.x},\"y\":${disposition.y}}")
        sb.append("}}")
        return sb.toString()
    }

    /**
     * Builder by Effective Java
     */
    static class Builder {
        // Required parameters
        private int size

        // Optional parameters - initialized to default values
        private Dimension windowDimension = new Dimension(1280, 600)
        private Point disposition = new Point(80, 80)

        Builder(int size) {
            if (size <= 0) {
                throw new IllegalArgumentException("size=${size} must not be <=0")
            }
            this.size = size
        }

        Builder windowDimension(Dimension windowDimension) {
            this.windowDimension = windowDimension
            return this
        }

        Builder disposition(Point disposition) {
            this.disposition = disposition
            return this
        }

        StackingWindowLayoutMetrics build() {
            return new StackingWindowLayoutMetrics(this)
        }
    }

}
