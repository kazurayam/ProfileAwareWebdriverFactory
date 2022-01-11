package com.kazurayam.browserwindowlayout;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

public class StackingWindowLayoutMetrics extends WindowLayoutMetrics {
    private StackingWindowLayoutMetrics(Builder builder) {
        size = builder.size;
        windowDimension = builder.windowDimension;
        disposition = builder.disposition;
    }

    public Dimension getWindowDimension() {
        return this.windowDimension;
    }

    public Point getDisposition() {
        return this.disposition;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public Point getWindowPosition(int windowIndex) {
        if (windowIndex < 0 || windowIndex >= this.size) {
            throw new IllegalArgumentException("windowIndex must be >=0 and <" + String.valueOf(this.getSize()));
        }

        int x = disposition.getX() * windowIndex;
        int y = disposition.getY() * windowIndex;
        return new Point(x, y);
    }

    @Override
    public Dimension getWindowDimension(int windowIndex) {
        return this.windowDimension;
    }

    @Override
    public boolean equals(Object o) {
        if (DefaultGroovyMethods.is(o, this)) {
            return true;
        }

        if (!(o instanceof StackingWindowLayoutMetrics)) {
            return false;
        }

        StackingWindowLayoutMetrics other = (StackingWindowLayoutMetrics) o;
        return this.windowDimension.equals(other.getWindowDimension()) && this.disposition.equals(other.getDisposition());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.windowDimension.hashCode();
        result = 31 * result + this.disposition.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"StackingWindowLayoutMetrics\":{");
        sb.append("\"size\":" + String.valueOf(this.getSize()));
        sb.append(",");
        sb.append("\"windowDimension\":{\"width\":" + String.valueOf(getWindowDimension().getWidth()) + ",\"height\":" + String.valueOf(getWindowDimension().getHeight()) + "}");
        sb.append(",");
        sb.append("\"disposition\":{\"x\":" + String.valueOf(getDisposition().getX()) + ",\"y\":" + String.valueOf(getDisposition().getY()) + "}");
        sb.append("}}");
        return sb.toString();
    }

    private final int size;
    private final Dimension windowDimension;
    private final Point disposition;

    /**
     * Builder by Effective Java
     */
    public static class Builder {
        public Builder(final int size) {
            if (size <= 0) {
                throw new IllegalArgumentException("size=" + String.valueOf(size) + " must not be <=0");
            }

            this.size = size;
        }

        public Builder windowDimension(Dimension windowDimension) {
            this.windowDimension = windowDimension;
            return this;
        }

        public Builder disposition(Point disposition) {
            this.disposition = disposition;
            return this;
        }

        public StackingWindowLayoutMetrics build() {
            return new StackingWindowLayoutMetrics(this);
        }

        private int size;
        private Dimension windowDimension = new Dimension(1280, 600);
        private Point disposition = new Point(80, 80);
    }
}
