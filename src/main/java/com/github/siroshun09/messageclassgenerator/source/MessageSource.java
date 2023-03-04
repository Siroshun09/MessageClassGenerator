package com.github.siroshun09.messageclassgenerator.source;

import java.util.Map;

public record MessageSource(String sourceName, String packageName, String className, Map<String, String> messageMap) {
}
