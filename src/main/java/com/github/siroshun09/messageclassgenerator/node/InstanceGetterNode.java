package com.github.siroshun09.messageclassgenerator.node;

import com.github.siroshun09.messageclassgenerator.util.IndentingWriter;
import com.github.siroshun09.messageclassgenerator.util.JavadocGenerator;

public final class InstanceGetterNode {

    public static Node staticConstant(String className, String fieldName) {
        return new StaticConstantNode(className, fieldName);
    }

    public static Node getter(String className, String getterName) {
        return new StaticConstantNode(className, getterName);
    }

    record StaticConstantNode(String className, String fieldName) implements Node {
        @Override
        public void write(IndentingWriter writer) {
            write(writer, true);
        }

        void write(IndentingWriter writer, boolean isPublic) {
            if (isPublic) {
                JavadocGenerator.generate(writer, "A " + JavadocGenerator.classLink(className) + " instance.");
            }

            var accessLevel = isPublic ? "public" : "private";
            writer.writeLine(accessLevel + " static final " + className + " " + fieldName + " = new " + className + "();");
            writer.writeEmptyLine();
            writer.writeLine("private " + className + "() {");
            writer.writeLine("}");
        }
    }

    record GetterNode(String className, String getterName) implements Node {

        private static final String INSTANCE_FIELD_NAME = "INSTANCE";

        @Override
        public void write(IndentingWriter writer) {
            new StaticConstantNode(className, INSTANCE_FIELD_NAME).write(writer, false);

            writer.writeEmptyLine();
            var doc = "the " + JavadocGenerator.classLink(className) + "instance";
            JavadocGenerator.generate(writer, "Gets" + doc + ".", "", "@returns " + doc);
            writer.writeLine("public static " + className + " " + getterName + "() {");
            writer.increaseIndent();
            writer.writeLine("return " + INSTANCE_FIELD_NAME + ";");
            writer.decreaseIndent();
            writer.writeLine("}");
        }
    }
}
