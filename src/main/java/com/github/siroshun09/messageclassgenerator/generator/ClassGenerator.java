package com.github.siroshun09.messageclassgenerator.generator;

import com.github.siroshun09.messageclassgenerator.context.Context;
import com.github.siroshun09.messageclassgenerator.node.RootNode;
import com.github.siroshun09.messageclassgenerator.processor.message.GetterRequired;
import com.github.siroshun09.messageclassgenerator.util.IndentingWriter;
import com.github.siroshun09.messageclassgenerator.util.Naming;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public final class ClassGenerator {

    private static final Logger LOGGER = Logging.getLogger(ClassGenerator.class);

    public static void generate(Context context) {
        generate(context, true);
    }

    public static void generate(Context context, boolean throwError) {
        try {
            generate0(context, throwError);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void generate0(Context context, boolean throwError) throws IOException {
        var rootNode = new RootNode(context.source().packageName(), context.source().className(), context.source().sourceName());

        if (context.messageProcessor() instanceof GetterRequired getterRequired) {
            getterRequired.addGetter(rootNode);
        }

        try {
            context.source().messageMap().forEach((key, value) -> context.messageProcessor().processMessage(rootNode, key, value));
        } catch (RuntimeException e) {
            if (throwError) {
                throw e;
            } else {
                LOGGER.error(e.getMessage());
                rootNode.clear();
            }
            return;
        }

        var outputDir = Naming.resolvePackagePath(context.directory(), context.source().packageName());

        if (!Files.isDirectory(outputDir)) {
            Files.createDirectories(outputDir);
        }

        var outputFile = outputDir.resolve(context.source().className() + ".java");

        try (var writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            rootNode.write(new IndentingWriter(writer, context.indentSpaces()));
        }
    }

    private ClassGenerator() {
        throw new UnsupportedOperationException();
    }
}
