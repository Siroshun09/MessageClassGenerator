package com.github.siroshun09.messageclassgenerator.watch;

import com.github.siroshun09.messageclassgenerator.source.MessageSource;
import com.github.siroshun09.messageclassgenerator.source.Watchable;
import io.methvin.watcher.DirectoryChangeEvent;
import io.methvin.watcher.DirectoryWatcher;
import org.gradle.api.logging.Logging;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.Consumer;

public class DirectoryWatcherWrapper {

    private final Watchable watchable;
    private final Consumer<MessageSource> messageSourceConsumer;

    public DirectoryWatcherWrapper(@NotNull Watchable watchable, @NotNull Consumer<MessageSource> messageSourceConsumer) {
        this.watchable = watchable;
        this.messageSourceConsumer = messageSourceConsumer;
    }

    public void watching() throws IOException {
        DirectoryWatcher watcher = null;

        try {
            watcher = createWatcher();
            watcher.watch();
        } finally {
            if (watcher != null) {
                watcher.close();
            }
        }
    }

    private DirectoryWatcher createWatcher() throws IOException {
        return DirectoryWatcher.builder()
                .path(watchable.directory())
                .listener(event -> {
                    if (event.eventType() == DirectoryChangeEvent.EventType.CREATE || event.eventType() == DirectoryChangeEvent.EventType.MODIFY) {
                        var source = watchable.createSource(event.path());

                        if (source != null) {
                            messageSourceConsumer.accept(source);
                        }
                    }
                })
                .logger(Logging.getLogger(DirectoryWatcher.class))
                .build();
    }
}
