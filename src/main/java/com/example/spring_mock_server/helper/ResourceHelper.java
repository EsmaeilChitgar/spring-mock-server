package com.example.spring_mock_server.helper;

import org.springframework.util.ResourceUtils;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

public class ResourceHelper {
    public static String readJsonFile(String fileName) {
        try {
            return StreamUtils.copyToString(
                    ResourceUtils.getURL("classpath:" + fileName).openStream(),
                    StandardCharsets.UTF_8
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON file: " + fileName, e);
        }
    }

    public static String readXmlFile(String fileName) {
        try {
            return StreamUtils.copyToString(
                    ResourceUtils.getURL("classpath:" + fileName).openStream(),
                    StandardCharsets.UTF_8
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to read XML file: " + fileName, e);
        }
    }
}
