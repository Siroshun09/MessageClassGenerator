package com.github.siroshun09.messageclassgenerator.processor.properties;

import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public final class PropertiesProcessor {

    public static @Unmodifiable Map<String, String> load(Path filepath) {
        var filename = filepath.getFileName();

        if (filename == null || !filename.toString().endsWith(".properties")) {
            return Collections.emptyMap();
        }

        var properties = new Properties();

        try (var reader = Files.newBufferedReader(filepath)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return PropertiesProcessor.process(properties);
    }

    public static @Unmodifiable Map<String, String> process(Properties properties) {
        var result = new LinkedHashMap<String, String>(properties.size(), 2.0f);

        properties.entrySet()
                .stream()
                .map(KeyedMessage::fromEntry)
                .sorted(Comparator.comparing(KeyedMessage::key))
                .forEachOrdered(message -> result.put(message.key, message.value));

        return Collections.unmodifiableMap(result);
    }

    private record KeyedMessage(String key, String value) {

        private static KeyedMessage fromEntry(Map.Entry<?, ?> entry) {
            if (entry.getKey() instanceof String key && entry.getValue() instanceof String value) {
                return new KeyedMessage(key, value);
            } else {
                throw new IllegalStateException("key or value is not String (key: " + entry.getKey() + " value: " + entry.getValue() + ")");
            }
        }

    }
}
