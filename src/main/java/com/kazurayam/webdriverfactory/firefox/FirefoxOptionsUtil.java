package com.kazurayam.webdriverfactory.firefox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.firefox.FirefoxOptions;

public class FirefoxOptionsUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private FirefoxOptionsUtil() {}

    public static String toJson(FirefoxOptions options) {
        JsonNode root = mapper.convertValue(options.asMap(), JsonNode.class);
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("unable to convert a FirefoxOptions instance into JSON", e);
        }
    }
}
