package com.github.siroshun09.messageclassgenerator.processor.message;

import com.github.siroshun09.messageclassgenerator.node.RootNode;

public interface MessageProcessor {

    void processMessage(RootNode rootNode, String messageKey, String defaultMessage);

}
