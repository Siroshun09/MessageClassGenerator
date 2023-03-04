package com.github.siroshun09.messageclassgenerator.source;

import java.util.stream.Stream;

public interface MessageSourceSupplier {

    Stream<MessageSource> stream();

}
