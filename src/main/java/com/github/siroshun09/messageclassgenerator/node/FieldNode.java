package com.github.siroshun09.messageclassgenerator.node;

import com.github.siroshun09.messageclassgenerator.util.IndentingWriter;
import com.github.siroshun09.messageclassgenerator.util.JavadocGenerator;

public record FieldNode(String fieldName, String fullKey, String message, boolean staticField) implements Node {

    @Override
    public void write(IndentingWriter writer) {
        JavadocGenerator.generate(writer, "key: {@code " + fullKey + "}<br/>", "message: " + message.replace("<", "&lt;").replace(">", "&gt;"));

        var prefix = staticField ? "public static" : "public";
        writer.writeLine(prefix + " final TranslatableComponent " + fieldName + " = Component.translatable(\"" + fullKey + "\");");
    }

}
