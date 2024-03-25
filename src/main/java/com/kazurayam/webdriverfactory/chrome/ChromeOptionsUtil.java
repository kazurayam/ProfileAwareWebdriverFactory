package com.kazurayam.webdriverfactory.chrome;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeOptionsUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private ChromeOptionsUtil() {}

    public static String toJson(ChromeOptions options) {
        JsonNode root = mapper.convertValue(options.asMap(), JsonNode.class);
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("unable to convert a ChromeOptions instance into JSON", e);
        }
    }

}
