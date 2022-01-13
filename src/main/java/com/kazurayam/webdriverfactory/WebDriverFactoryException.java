package com.kazurayam.webdriverfactory;

public class WebDriverFactoryException extends Exception {
    public WebDriverFactoryException(String msg) {
        super(msg);
    }

    public WebDriverFactoryException(String msg, Throwable t) {
        super(msg, t);
    }
}
