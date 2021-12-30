package com.kazurayam.browserwindowlayout

class WindowLocation {

    public static final WindowLocation DEFAULT = new WindowLocation(1, 0)

    public final int size
    public final int index

    WindowLocation(int size, int index) {
        validate(size, index)
        this.size = size
        this.index = index
    }

    @Override
    boolean equals(Object o) {
        if (o.is(this)) {
            return true
        }
        if (!(o instanceof WindowLocation)) {
            return false
        }
        WindowLocation other = (WindowLocation)o
        return this.size == other.size && this.index == other.index
    }

    @Override
    int hashCode() {
        int result = 17
        result = 31 * result + this.size
        result = 31 * result + this.index
        return result
    }

    @Override
    String toString() {
        return "{\"WindowLocation\":{\"size\":${size},\"index\":${index}}}"
    }

    static void validate(int size, int index) {
        if (size <= 0) {
            throw new IllegalArgumentException("size=${size}, must be greater than 0")
        }
        if (index < 0) {
            throw new IllegalArgumentException("index=${index}, must be >= 0")
        }
        if (size - 1 < index) {
            throw new IllegalArgumentException("index=${index}, must be < size(${size})")
        }
    }

}
