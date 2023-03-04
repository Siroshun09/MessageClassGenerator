package com.github.siroshun09.messageclassgenerator.util;

import com.github.siroshun09.messageclassgenerator.node.Node;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

public class IndentingWriter {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
    private final Writer writer;
    private final String indent;
    private int depth = 0;

    public IndentingWriter(@NotNull Writer writer) {
        this(writer, 4);
    }

    public IndentingWriter(@NotNull Writer writer, int indentSpaces) {
        this.writer = writer;
        this.indent = createIndent(indentSpaces);
    }

    public void increaseIndent() {
        depth++;
    }

    public void decreaseIndent() {
        depth--;
    }

    public void writeNodes(Collection<Node> nodes) {
        int count = 0;
        int size = nodes.size();

        for (var node : nodes) {
            writeEmptyLine();

            node.write(this);

            if (++count == size) {
                writeEmptyLine();
            }
        }
    }

    public void writeLine(@NotNull String line) {
        writeIndent();
        write(line);
        write(LINE_SEPARATOR);
    }

    public void writeEmptyLine() {
        write(LINE_SEPARATOR);
    }

    private void writeIndent() {
        if (depth == 0) {
            return;
        }

        for (int i = 1; i <= depth; i++) {
            write(indent);
        }
    }

    private void write(@NotNull String str) {
        try {
            writer.write(str);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String createIndent(int spaces) {
        return " ".repeat(Math.max(0, spaces));
    }
}
