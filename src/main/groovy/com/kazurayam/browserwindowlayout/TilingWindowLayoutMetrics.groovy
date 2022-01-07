package com.kazurayam.browserwindowlayout

import org.openqa.selenium.Dimension
import org.openqa.selenium.Point

import java.awt.Toolkit

class TilingWindowLayoutMetrics extends WindowLayoutMetrics {

    private final int size
    private final Dimension virtualScreenSize
    private final Point basePoint

    private TilingWindowLayoutMetrics(Builder builder) {
        this.size = builder.size
        virtualScreenSize = new Dimension(
                builder.physicalScreenSize.width - builder.basePoint.x * 2,
                builder.physicalScreenSize.height - builder.basePoint.y * 2
        )
        basePoint = builder.basePoint
    }

    Dimension getVirtualScreenSize() {
        return virtualScreenSize
    }

    Point getBasePoint() {
        return basePoint
    }

    @Override
    int getSize() {
        return this.size
    }

    @Override
    Dimension getWindowDimension(int windowIndex) {
        if (windowIndex < 0 || windowIndex >= this.size) {
            throw new IllegalArgumentException("windowIndex=${windowIndex} must not be <0 and >=size")
        }
        if (size == 1) {
            return virtualScreenSize
        } else {
            // Tiles in 2 columns
            int width = (int)Math.floor(virtualScreenSize.width / 2)
            int rows = (int)Math.ceil(this.size / 2)
            int height = (int)Math.floor(virtualScreenSize.height / rows )
            return new Dimension(width, height)
        }
    }

    @Override
    Point getWindowPosition(int windowIndex) {
        if (windowIndex < 0 || windowIndex >= this.size) {
            throw new IllegalArgumentException("windowIndex=${windowIndex} must not be <0 and >=size")
        }
        int x = basePoint.x + (windowIndex % 2) * this.getWindowDimension(windowIndex).width
        int y = basePoint.y + Math.floor(windowIndex / 2) * this.getWindowDimension(windowIndex).height
        return new Point(x, y)
    }

    @Override
    boolean equals(Object o) {
        if (o.is(this)) {
            return true
        }
        if (!(o instanceof TilingWindowLayoutMetrics)) {
            return false
        }
        TilingWindowLayoutMetrics other = (TilingWindowLayoutMetrics)o
        return this.virtualScreenSize == other.virtualScreenSize &&
                this.basePoint == other.basePoint
    }

    @Override
    int hashCode() {
        int result = 17
        result = 31 * result + this.virtualScreenSize.hashCode()
        result = 31 * result + this.basePoint.hashCode()
        return result
    }

    @Override
    String toString() {
        StringBuilder sb = new StringBuilder()
        sb.append("{\"TilingWindowLayoutMetrics\":{")
        sb.append("\"virtualScreenSize\":[${virtualScreenSize.width},${virtualScreenSize.height}]")
        sb.append(",")
        sb.append("\"basePoint\":[${basePoint.x},${basePoint.y}]")
        sb.append("}}")
        return sb.toString()
    }

    /**
     * Builder pattern by "Effective Java"
     */
    static class Builder {
        // Required parameters - none
        private int size

        // Optional parameters - initialized to default values
        private java.awt.Dimension ss = Toolkit.getDefaultToolkit().getScreenSize()
        private Dimension physicalScreenSize = new Dimension((int)ss.width, (int)ss.height)
        private Point basePoint = new Point(10, 10)

        Builder(int size) {
            if (size <= 0) {
                throw new IllegalArgumentException("size=${size} must not be <=0")
            }
            this.size = size
        }

        Builder physicalScreenSize(Dimension physicalScreenSize) {
            this.physicalScreenSize = physicalScreenSize
            return this
        }

        Builder basePoint(Point basePoint) {
            this.basePoint = basePoint
            return this
        }

        TilingWindowLayoutMetrics build() {
            return new TilingWindowLayoutMetrics(this)
        }
    }

}
