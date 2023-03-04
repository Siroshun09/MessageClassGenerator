package com.github.siroshun09.messageclassgenerator.node;

import com.github.siroshun09.messageclassgenerator.util.IndentingWriter;
import com.github.siroshun09.messageclassgenerator.util.JavadocGenerator;
import com.github.siroshun09.messageclassgenerator.util.Naming;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ClassNode implements Node {

    private final Map<String, Node> map = new LinkedHashMap<>();
    private final String fieldName;
    private final String fullKey;

    public ClassNode(String fieldName, String fullKey) {
        this.fieldName = fieldName;
        this.fullKey = fullKey;
    }

    public boolean hasNode(@NotNull String fieldName) {
        return map.containsKey(fieldName);
    }

    public @NotNull Node getOrCreateClassNode(@NotNull String fieldName, @NotNull String fullKey) {
        return map.computeIfAbsent(fieldName, $ -> new ClassNode(fieldName, fullKey));
    }

    public void putFieldNode(@NotNull String fieldName, @NotNull String fullKey, @NotNull String message) {
        map.put(fieldName, new FieldNode(fieldName, fullKey, message, false));
    }

    @Override
    public void write(@NotNull IndentingWriter writer) {
        var className = Naming.toClassName(fullKey);

        JavadocGenerator.generate(writer, "A " + JavadocGenerator.classLink(className) + " instance holding messages that are under the key '{@code " + fullKey + "}'");
        writer.writeLine("public final " + className + " " + fieldName + " = new " + className + "();");
        writer.writeEmptyLine();
        JavadocGenerator.generate(writer, "A class holding messages that are under the key '{@code " + fullKey + "}'");
        writer.writeLine("public static final class " + className + " {");
        writer.increaseIndent();

        writer.writeEmptyLine();
        writer.writeLine("private " + className + "() {");
        writer.writeLine("}");

        writer.writeNodes(map.values());

        writer.decreaseIndent();

        writer.writeLine("}");
    }
}
