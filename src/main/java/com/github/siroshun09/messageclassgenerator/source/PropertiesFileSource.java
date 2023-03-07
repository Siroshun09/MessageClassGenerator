package com.github.siroshun09.messageclassgenerator.source;

import com.github.siroshun09.messageclassgenerator.processor.properties.PropertiesProcessor;
import com.github.siroshun09.messageclassgenerator.util.Naming;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

public class PropertiesFileSource implements MessageSourceSupplier, Watchable {

    public final Path filepath;

    public PropertiesFileSource(Path filepath) {
        this.filepath = filepath;
    }

    public String packageName;
    public String className;

    @Override
    public Stream<MessageSource> stream() {
        if (!Files.isRegularFile(filepath)) {
            return Stream.of(createMessageSource(Collections.emptyMap()));
        }

        return Stream.of(createMessageSource());
    }

    @Override
    public Path directory() {
        return filepath.normalize().getParent();
    }

    @Override
    public @Nullable MessageSource createSource(Path changedPath) {
        return changedPath.getFileName().equals(filepath.getFileName()) ? createMessageSource() : null;
    }

    private MessageSource createMessageSource() {
        return createMessageSource(PropertiesProcessor.load(filepath));
    }

    private MessageSource createMessageSource(Map<String, String> messageMap) {
        var className = this.className != null ? this.className : Naming.toClassName(filepath.getFileName().toString().replace(".properties", "")) + "Messages";
        return new MessageSource(filepath.getFileName().toString(), packageName, className, messageMap);
    }
}
