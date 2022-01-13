package com.kazurayam.webdriverfactory.chrome;

import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

@FunctionalInterface
public interface ChromeOptionsModifyFunction {

	ChromeOptions modify(ChromeOptions options, List<Object> arguments);

}
