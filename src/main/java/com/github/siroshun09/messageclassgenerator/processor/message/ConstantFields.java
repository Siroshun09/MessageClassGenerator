package com.github.siroshun09.messageclassgenerator.processor.message;

import com.github.siroshun09.messageclassgenerator.node.RootNode;
import com.github.siroshun09.messageclassgenerator.util.Naming;
import org.gradle.api.Action;

import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;

public class ConstantFields implements MessageProcessor {

    private final KeyEditor keyEditor = new KeyEditor();

    @Override
    public void processMessage(RootNode rootNode, String messageKey, String defaultMessage) {
        Objects.requireNonNull(messageKey);
        Objects.requireNonNull(defaultMessage);

        var filteredKey = keyEditor.edit(messageKey);
        var fieldName = Naming.toConstantFieldName(filteredKey);

        if (rootNode.hasNode(fieldName)) {
            throw new IllegalStateException(fieldName + " already exists. Are there any duplicate keys after filtering?");
        }

        rootNode.putFieldNode(fieldName, messageKey, defaultMessage, true);
    }

    public void keyEditor(Action<KeyEditor> action) {
        action.execute(keyEditor);
    }

    public static final class KeyEditor {

        private Function<String, String> editor = Function.identity();

        private String edit(String key) {
            return editor.apply(key);
        }

        public void removePrefix(String prefix) {
            function(key -> {
                if (key.startsWith(prefix)) {
                    return key.substring(prefix.length());
                } else {
                    return key;
                }
            });
        }

        public void removeMatching(String regex) {
            removeMatching(Pattern.compile(regex));
        }

        public void removeMatching(Pattern pattern) {
            function(key -> pattern.matcher(key).replaceAll(""));
        }

        public void function(Function<String, String> function) {
            editor = editor.andThen(function);
        }
    }
}
