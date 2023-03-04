package com.github.siroshun09.messageclassgenerator.context;

import com.github.siroshun09.messageclassgenerator.processor.message.MessageProcessor;
import com.github.siroshun09.messageclassgenerator.source.MessageSource;

import java.nio.file.Path;
import java.util.Objects;

public record Context(MessageSource source, Path directory, MessageProcessor messageProcessor, int indentSpaces) {
    public Context(MessageSource source, Path directory, MessageProcessor messageProcessor, int indentSpaces) {
        this.source = Objects.requireNonNull(source);
        this.directory = Objects.requireNonNull(directory);
        this.messageProcessor = Objects.requireNonNull(messageProcessor);
        this.indentSpaces = indentSpaces;
    }
}
