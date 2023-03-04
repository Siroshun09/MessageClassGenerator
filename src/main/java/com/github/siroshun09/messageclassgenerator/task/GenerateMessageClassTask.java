package com.github.siroshun09.messageclassgenerator.task;

import com.github.siroshun09.messageclassgenerator.context.Context;
import com.github.siroshun09.messageclassgenerator.generator.ClassGenerator;
import com.github.siroshun09.messageclassgenerator.processor.message.ConstantFields;
import com.github.siroshun09.messageclassgenerator.processor.message.MessageProcessor;
import com.github.siroshun09.messageclassgenerator.source.DirectorySource;
import com.github.siroshun09.messageclassgenerator.source.MessageSource;
import com.github.siroshun09.messageclassgenerator.source.MessageSourceSupplier;
import com.github.siroshun09.messageclassgenerator.source.PropertiesFileSource;
import com.github.siroshun09.messageclassgenerator.source.Watchable;
import com.github.siroshun09.messageclassgenerator.util.FileUtils;
import com.github.siroshun09.messageclassgenerator.util.GradleDirs;
import com.github.siroshun09.messageclassgenerator.util.JavaExtensionUtils;
import com.github.siroshun09.messageclassgenerator.watch.DirectoryWatcherWrapper;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.Path;

public class GenerateMessageClassTask extends DefaultTask {

    public static final String TASK_NAME = "generateMessageClass";
    public static final String WATCH_MESSAGE_FILES_TASK_NAME = "watchMessageFiles";

    @OutputDirectory
    private final Path generatedClassDir = GradleDirs.cacheDir(getProject()).resolve("generated-message-classes");
    @Internal
    private MessageSourceSupplier messageSourceSupplier;
    @Internal
    private MessageProcessor messageProcessor;

    @TaskAction
    public void execute() throws IOException {
        if (getMessageSourceSupplier() == null) {
            return;
        }

        FileUtils.deleteDirectory(generatedClassDir);

        try (var stream = getMessageSourceSupplier().stream()) {
            stream.map(this::createContext).forEach(ClassGenerator::generate);
        }
    }

    public void execute(MessageSource messageSource, boolean throwError) {
        ClassGenerator.generate(createContext(messageSource), throwError);
    }

    private Context createContext(MessageSource source) {
        return new Context(
                source,
                generatedClassDir,
                getMessageProcessor() != null ? getMessageProcessor() : new ConstantFields(),
                4
        );
    }

    public MessageSourceSupplier getMessageSourceSupplier() {
        return messageSourceSupplier;
    }

    public void setMessageSourceSupplier(MessageSourceSupplier messageSourceSupplier) {
        this.messageSourceSupplier = messageSourceSupplier;

        if (messageSourceSupplier instanceof Watchable watchable) {
            getProject().getTasks().register(
                    WATCH_MESSAGE_FILES_TASK_NAME,
                    task -> task.doLast($ -> {
                                try {
                                    new DirectoryWatcherWrapper(watchable, source ->  ClassGenerator.generate(createContext(source), false)).watching();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    )
            );
        }
    }

    public Path getGeneratedClassDir() {
        return generatedClassDir;
    }

    public MessageProcessor getMessageProcessor() {
        return messageProcessor;
    }

    public void setMessageProcessor(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    public ConstantFields constantFields() {
        return constantFields(processor -> {
        });
    }

    public ConstantFields constantFields(Action<ConstantFields> action) {
        var processor = new ConstantFields();
        action.execute(processor);
        return processor;
    }

    public PropertiesFileSource fromPropertiesFile(Path file) {
        return new PropertiesFileSource(file);
    }

    public DirectorySource fromDirectory(Path directory) {
        return new DirectorySource(directory);
    }

    public PropertiesFileSource fromPropertiesFileInResourceDir(String filename) {
        return fromPropertiesFile(GradleDirs.resourceDir(getProject()).resolve(filename));
    }

    public DirectorySource fromDirectoryInResourceDir(String directoryName) {
        JavaExtensionUtils.getMainSourceSet(getProject()).getResources().exclude(element -> element.isDirectory() && element.getPath().equals(directoryName));
        return fromDirectory(GradleDirs.resourceDir(getProject()).resolve(directoryName));
    }
}
