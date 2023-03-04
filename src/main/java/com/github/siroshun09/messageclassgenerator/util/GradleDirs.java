package com.github.siroshun09.messageclassgenerator.util;

import org.gradle.api.Project;

import java.nio.file.Path;

public final class GradleDirs {


    public static Path resourceDir(Project project) {
        return project.getLayout().getProjectDirectory().dir("src/main/resources").getAsFile().toPath();
    }

    public static Path cacheDir(Project project) {
        return project.getLayout().getProjectDirectory().dir(".gradle/caches").dir("message-class-generator").getAsFile().toPath();
    }

}
