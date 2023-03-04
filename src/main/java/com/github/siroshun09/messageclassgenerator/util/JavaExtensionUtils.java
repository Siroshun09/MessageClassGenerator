package com.github.siroshun09.messageclassgenerator.util;

import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;

public final class JavaExtensionUtils {
    public static SourceSet getMainSourceSet(Project project) {
        return project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().named(SourceSet.MAIN_SOURCE_SET_NAME).get();
    }
}
