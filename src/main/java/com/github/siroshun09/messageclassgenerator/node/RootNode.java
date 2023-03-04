package com.github.siroshun09.messageclassgenerator.node;

import com.github.siroshun09.messageclassgenerator.util.IndentingWriter;
import com.github.siroshun09.messageclassgenerator.util.JavadocGenerator;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public final class RootNode implements Node {

    private final Map<String, Node> rootMap = new LinkedHashMap<>();

    private final String packageName;
    private final String className;
    private final String sourceName;
    private Node instanceGetter;

    public RootNode(String packageName, String className, @Nullable String sourceName) {
        this.packageName = packageName;
        this.className = className;
        this.sourceName = sourceName;
    }

    public void addInstanceGetter(Node getter) {
        this.instanceGetter = getter;
    }

    public boolean hasNode(String fieldName) {
        return rootMap.containsKey(fieldName);
    }

    public Node getOrCreateClassNode(String fieldName, String parentKeys) {
        return rootMap.computeIfAbsent(fieldName, $ -> new ClassNode(fieldName, parentKeys));
    }

    public void putFieldNode(String fieldName, String fullKey, String message, boolean staticField) {
        rootMap.put(fieldName, new FieldNode(fieldName, fullKey, message, staticField));
    }

    public void clear() {
        rootMap.clear();
    }

    @Override
    public void write(IndentingWriter writer) {
        if (!packageName.isEmpty()) {
            writer.writeLine("package " + packageName + ";");
            writer.writeEmptyLine();
        }

        writer.writeLine("import net.kyori.adventure.text.Component;");
        writer.writeLine("import net.kyori.adventure.text.TranslatableComponent;");
        writer.writeEmptyLine();

        var suffix = sourceName != null ? " (generated from {@code " + sourceName + "})" : ".";

        JavadocGenerator.generate(writer, "A auto-generated message class" + suffix);
        writer.writeLine("public final class " + className + " {");

        writer.increaseIndent();

        if (instanceGetter != null) {
            writer.writeEmptyLine();
            instanceGetter.write(writer);
        }

        writer.writeNodes(rootMap.values());

        writer.decreaseIndent();

        writer.writeLine("}");
    }
}
