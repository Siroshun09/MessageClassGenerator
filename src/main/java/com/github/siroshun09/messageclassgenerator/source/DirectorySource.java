package com.github.siroshun09.messageclassgenerator.source;

import com.github.siroshun09.messageclassgenerator.processor.properties.PropertiesProcessor;
import com.github.siroshun09.messageclassgenerator.util.Naming;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class DirectorySource implements MessageSourceSupplier, Watchable {

    public final Path directory;

    public String rootPackageName;

    public final FilenameToClassName filenameToClassName = new FilenameToClassName();

    public DirectorySource(Path directory) {
        this.directory = directory;
    }

    @SuppressWarnings("resource")
    @Override
    public Stream<MessageSource> stream() {
        if (!Files.isDirectory(directory)) {
            return Stream.empty();
        }

        try {
            return Files.walk(directory).filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".properties"))
                    .map(this::createMessageSource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path directory() {
        return directory;
    }

    @Override
    public @Nullable MessageSource createSource(Path changedPath) {
        if (Files.isRegularFile(changedPath) && changedPath.getFileName().toString().endsWith(".properties")) {
            return createMessageSource(changedPath.toAbsolutePath());
        } else {
            return null;
        }
    }

    private MessageSource createMessageSource(Path filepath) {
        return new MessageSource(
                toInvariantSeparatorsPath(directory.relativize(filepath)),
                createPackageName(directory.relativize(filepath.getParent())),
                filenameToClassName.apply(filepath.getFileName().toString()),
                PropertiesProcessor.load(filepath)
        );
    }

    private String createPackageName(Path relativePath) {
        var relativePackageName = Naming.toPackageName(relativePath);

        if (rootPackageName.isEmpty()) {
            return relativePackageName;
        } else {
            return relativePackageName.isEmpty() ?
                    rootPackageName :
                    rootPackageName + "." + relativePackageName;
        }
    }

    private String toInvariantSeparatorsPath(Path path) {
        return path.getFileSystem().getSeparator().equals("/") ?
                path.toString() :
                path.toString().replace(path.getFileSystem().getSeparator(), "/");
    }

    public static class FilenameToClassName {

        private UnaryOperator<String> operator = filename -> Naming.toClassName(filename.replace(".properties", "")) + "Messages";

        private FilenameToClassName() {
        }

        public @NotNull String apply(@NotNull String filename) {
            return operator.apply(filename);
        }

        public void suffixed(@NotNull String suffix) {
            operator = filename -> Naming.toClassName(filename.replace(".properties", "")) + suffix;
        }
    }
}
