module idk.runner {
    exports com.github.natanbc.idk.runner;
    
    requires transitive idk.ast;
    requires transitive idk.bytecode;
    requires transitive idk.common;
    requires transitive idk.compiler;
    requires transitive idk.interpreter;
    requires transitive idk.ir;
    requires transitive idk.parser;
    requires transitive idk.runtime;
    requires transitive idk.stdlib;
}