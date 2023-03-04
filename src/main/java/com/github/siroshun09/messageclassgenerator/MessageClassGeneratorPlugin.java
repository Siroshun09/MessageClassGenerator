package com.github.siroshun09.messageclassgenerator;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public class MessageClassGeneratorPlugin implements Plugin<Project> {
    @Override
    public void apply(@NotNull Project target) {
        target.getExtensions().create("messageClassGenerator", MessageClassGeneratorExtension.class);
    }
}
