package com.github.siroshun09.messageclassgenerator.source;

import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public interface Watchable {

    Path directory();

    @Nullable MessageSource createSource(Path changedPath);

}
