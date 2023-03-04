package com.github.siroshun09.messageclassgenerator.node;

import com.github.siroshun09.messageclassgenerator.util.IndentingWriter;

public sealed interface Node permits RootNode, ClassNode, FieldNode, InstanceGetterNode.GetterNode, InstanceGetterNode.StaticConstantNode {

    void write(IndentingWriter writer);

}
