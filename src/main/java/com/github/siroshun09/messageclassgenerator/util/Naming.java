package com.github.siroshun09.messageclassgenerator.util;

import java.nio.file.Path;
import java.util.Set;
import java.util.regex.Pattern;

public final class Naming {

    /**
     * <a href="https://docs.oracle.com/javase/specs/jls/se17/html/jls-3.html#jls-3.9">JLS</a>
     */
    private static final Set<String> JAVA_KEYWORDS = Set.of(
            "abstract", "continue", "for", "new", "switch", "assert", "default",
            "if", "package", "synchronized", "boolean", "do", "goto", "private",
            "this", "break", "double", "implements", "protected", "throw", "byte",
            "else", "import", "public", "throws", "case", "enum", "instanceof",
            "return", "transient", "catch", "extends", "int", "short", "try",
            "char", "final", "interface", "static", "void", "class", "finally",
            "long", "strictfp", "volatile", "const", "float", "native", "super", "while"
    );

    public static String toFieldName(String original) {
        var builder = new StringBuilder();
        boolean toUppercase = false;

        for (int codePoint : original.codePoints().toArray()) {
            boolean isSeparator = codePoint == '-' || codePoint == '_' || codePoint == '.';

            if (isSeparator) {
                if (toUppercase) {
                    throw new IllegalArgumentException("invalid: " + original);
                } else {
                    toUppercase = true;
                    continue;
                }
            }

            if (toUppercase) {
                builder.appendCodePoint(Character.toUpperCase(codePoint));
                toUppercase = false;
            } else {
                builder.appendCodePoint(codePoint);
            }
        }

        var result = builder.toString();

        if (JAVA_KEYWORDS.contains(result)) {
            throw new IllegalStateException(result + " is Java's keyword.");
        }

        return result;
    }

    public static String toClassName(String original) {
        return fieldNameToClassName(toFieldName(original));
    }

    public static String toConstantFieldName(String original) {
        var builder = new StringBuilder();

        for (int codePoint : original.codePoints().toArray()) {
            boolean isSeparator = codePoint == '-' || codePoint == '_' || codePoint == '.';

            if (isSeparator) {
                builder.append('_');
            } else {
                builder.appendCodePoint(Character.toUpperCase(codePoint));
            }
        }

        return builder.toString();
    }

    public static String toPackageName(Path relativePath) {
        var separator = relativePath.getFileSystem().getSeparator();
        return relativePath.toString().replace(separator, ".");
    }

    private static final Pattern PACKAGE_SEPARATOR = Pattern.compile(".", Pattern.LITERAL);

    public static Path resolvePackagePath(Path directory, String packageName) {
        if (packageName.isEmpty()) {
            return directory;
        }

        var result = directory;

        for (var element : PACKAGE_SEPARATOR.split(packageName)) {
            result = result.resolve(element);
        }

        return result;
    }

    public static String fieldNameToClassName(String fieldName) {
        int len = fieldName.length();
        if (len == 0) {
            throw new IllegalArgumentException("empty string");
        }

        // StringUtils#capitalize (commons-lang3)
        final int firstCodepoint = fieldName.codePointAt(0);
        final int newCodePoint = Character.toTitleCase(firstCodepoint);
        if (firstCodepoint == newCodePoint) {
            // already capitalized
            return fieldName;
        }

        final int[] newCodePoints = new int[len]; // cannot be longer than the char array
        int outOffset = 0;
        newCodePoints[outOffset++] = newCodePoint; // copy the first codepoint
        for (int inOffset = Character.charCount(firstCodepoint); inOffset < len; ) {
            final int codepoint = fieldName.codePointAt(inOffset);
            newCodePoints[outOffset++] = codepoint; // copy the remaining ones
            inOffset += Character.charCount(codepoint);
        }

        return new String(newCodePoints, 0, outOffset);
    }

    private Naming() {
        throw new UnsupportedOperationException();
    }
}
