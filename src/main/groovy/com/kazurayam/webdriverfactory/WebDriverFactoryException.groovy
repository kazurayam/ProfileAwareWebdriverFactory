package com.kazurayam.webdriverfactory

class WebDriverFactoryException extends Exception{

    WebDriverFactoryException(String msg) {
        super(msg)
    }

    WebDriverFactoryException(String msg, Throwable t) {
        super(msg, t)
    }
}
