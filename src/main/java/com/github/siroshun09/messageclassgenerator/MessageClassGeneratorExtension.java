package com.github.siroshun09.messageclassgenerator;

import com.github.siroshun09.messageclassgenerator.task.CollectMessagesTask;
import com.github.siroshun09.messageclassgenerator.task.GenerateMessageClassTask;
import com.github.siroshun09.messageclassgenerator.util.JavaExtensionUtils;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.plugins.ide.idea.IdeaPlugin;

public abstract class MessageClassGeneratorExtension {

    private final Project project;

    public MessageClassGeneratorExtension(Project project) {
        this.project = project;
    }

    public TaskProvider<GenerateMessageClassTask> registerGenerateMessageClassTask() {
        return registerGenerateMessageClassTask(task -> {
        });
    }

    public TaskProvider<GenerateMessageClassTask> registerGenerateMessageClassTask(Action<GenerateMessageClassTask> configure) {
        var task = project.getTasks().register(GenerateMessageClassTask.TASK_NAME, GenerateMessageClassTask.class);

        project.getTasks().getByName("compileJava").dependsOn(GenerateMessageClassTask.TASK_NAME);

        configure.execute(task.get());

        project.afterEvaluate($ -> {
            var classDir = task.get().getGeneratedClassDir();
            var module = project.getPlugins().apply(IdeaPlugin.class).getModel().getModule();

            JavaExtensionUtils.getMainSourceSet(project).getJava().srcDirs(classDir);

            module.getSourceDirs().add(classDir.toFile());
            module.getGeneratedSourceDirs().add(classDir.toFile());
        });

        return task;
    }

    public TaskProvider<CollectMessagesTask> registerCollectMessagesTask() {
        return registerCollectMessagesTask(task -> {
        });
    }

    public TaskProvider<CollectMessagesTask> registerCollectMessagesTask(Action<CollectMessagesTask> configure) {
        var task = project.getTasks().register(CollectMessagesTask.TASK_NAME, CollectMessagesTask.class);

        project.getTasks().getByName("processResources").dependsOn(CollectMessagesTask.TASK_NAME);

        configure.execute(task.get());

        return task;
    }
}
