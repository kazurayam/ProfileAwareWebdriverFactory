package com.kazurayam.browserwindowlayout;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

import java.awt.*;

public class TilingWindowLayoutMetrics extends WindowLayoutMetrics {
    private TilingWindowLayoutMetrics(Builder builder) {
        this.size = builder.size;
        virtualScreenSize = new Dimension(builder.physicalScreenSize.getWidth() - builder.basePoint.getX() * 2, builder.physicalScreenSize.getHeight() - builder.basePoint.getY() * 2);
        basePoint = builder.basePoint;
    }

    public Dimension getVirtualScreenSize() {
        return virtualScreenSize;
    }

    public Point getBasePoint() {
        return basePoint;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public Dimension getWindowDimension(final int windowIndex) {
        if (windowIndex < 0 || windowIndex >= this.size) {
            throw new IllegalArgumentException("windowIndex=" + String.valueOf(windowIndex) + " must not be <0 and >=size");
        }

        if (size == 1) {
            return virtualScreenSize;
        } else {
            // Tiles in 2 columns
            int width = (int) Math.floor(virtualScreenSize.getWidth() / 2);
            int rows = (int) Math.ceil(this.size / 2);
            int height = (int) Math.floor(virtualScreenSize.getHeight() / rows);
            return new Dimension(width, height);
        }

    }

    @Override
    public Point getWindowPosition(final int windowIndex) {
        if (windowIndex < 0 || windowIndex >= this.size) {
            throw new IllegalArgumentException("windowIndex=" +
                    String.valueOf(windowIndex) + " must not be <0 and >=size");
        }

        int x = basePoint.getX() + (windowIndex % 2) * this.getWindowDimension(windowIndex).getWidth();
        int y = (int) ((int) basePoint.getY() + Math.floor(windowIndex / 2) * this.getWindowDimension(windowIndex).getHeight());
        return new Point(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (DefaultGroovyMethods.is(o, this)) {
            return true;
        }

        if (!(o instanceof TilingWindowLayoutMetrics)) {
            return false;
        }

        TilingWindowLayoutMetrics other = (TilingWindowLayoutMetrics) o;
        return this.virtualScreenSize.equals(other.getVirtualScreenSize()) && this.basePoint.equals(other.getBasePoint());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.virtualScreenSize.hashCode();
        result = 31 * result + this.basePoint.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"TilingWindowLayoutMetrics\":{");
        sb.append("\"virtualScreenSize\":[" + String.valueOf(getVirtualScreenSize().getWidth()) + "," + String.valueOf(getVirtualScreenSize().getHeight()) + "]");
        sb.append(",");
        sb.append("\"basePoint\":[" + String.valueOf(getBasePoint().getX()) + "," + String.valueOf(getBasePoint().getY()) + "]");
        sb.append("}}");
        return sb.toString();
    }

    private final int size;
    private final Dimension virtualScreenSize;
    private final Point basePoint;

    /**
     * Builder pattern by "Effective Java"
     */
    public static class Builder {
        public Builder(final int size) {
            if (size <= 0) {
                throw new IllegalArgumentException("size=" + String.valueOf(size) + " must not be <=0");
            }

            this.size = size;
        }

        public Builder physicalScreenSize(Dimension physicalScreenSize) {
            this.physicalScreenSize = physicalScreenSize;
            return this;
        }

        public Builder basePoint(Point basePoint) {
            this.basePoint = basePoint;
            return this;
        }

        public TilingWindowLayoutMetrics build() {
            return new TilingWindowLayoutMetrics(this);
        }

        private int size;
        private java.awt.Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
        private Dimension physicalScreenSize = new Dimension((int) ss.getWidth(), (int) ss.getHeight());
        private Point basePoint = new Point(10, 10);
    }
}
