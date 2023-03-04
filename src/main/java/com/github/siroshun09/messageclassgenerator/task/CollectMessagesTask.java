package com.github.siroshun09.messageclassgenerator.task;

import com.github.siroshun09.messageclassgenerator.source.MessageSource;
import com.github.siroshun09.messageclassgenerator.util.GradleDirs;
import com.github.siroshun09.messageclassgenerator.util.FileUtils;
import com.github.siroshun09.messageclassgenerator.util.IndentingWriter;
import com.github.siroshun09.messageclassgenerator.util.JavaExtensionUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class CollectMessagesTask extends DefaultTask {

    public static final String TASK_NAME = "collectMessages";

    @Input
    private String outputMessageFilename = "messages_en.properties";

    @TaskAction
    public void execute() throws IOException {
        var resourceDir = GradleDirs.cacheDir(getProject()).resolve("generated-resources");

        FileUtils.deleteDirectory(resourceDir);

        var collectedMessages = new HashMap<String, String>(100, 1.0f);

        for (var project : getProject().getRootProject().getAllprojects()) {
            if (!(project.getTasks().findByName("generateMessageClass") instanceof GenerateMessageClassTask task) || task.getMessageSourceSupplier() == null) {
                continue;
            }

            try (var stream = task.getMessageSourceSupplier().stream()) {
                stream.map(MessageSource::messageMap).forEach(collectedMessages::putAll);
            }
        }

        Files.createDirectories(resourceDir);

        var outputFile = resourceDir.resolve(getOutputMessageFilename());

        try (var writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            var indentingWriter = new IndentingWriter(writer);
            collectedMessages.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEachOrdered(entry -> indentingWriter.writeLine(entry.getKey() + "=" + entry.getValue().replace("\\", "\\\\")));
        }

        JavaExtensionUtils.getMainSourceSet(getProject()).getResources().srcDir(resourceDir);
    }

    public String getOutputMessageFilename() {
        return outputMessageFilename;
    }

    public void setOutputMessageFilename(String outputMessageFilename) {
        this.outputMessageFilename = outputMessageFilename;
    }
}
