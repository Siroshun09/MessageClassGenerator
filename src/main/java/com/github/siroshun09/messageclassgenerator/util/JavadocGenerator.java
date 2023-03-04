package com.github.siroshun09.messageclassgenerator.util;

public final class JavadocGenerator {

    public static void generate(IndentingWriter writer, String... comments) {
        writer.writeLine("/**");

        for (var comment : comments) {
            writer.writeLine(" * " + comment);
        }

        writer.writeLine(" */");
    }

    public static String classLink(String className) {
        return "{@link " + className + "}";
    }

    private JavadocGenerator() {
        throw new UnsupportedOperationException();
    }
}
