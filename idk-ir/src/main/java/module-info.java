module idk.ir {
    exports com.github.natanbc.idk.ir;
    exports com.github.natanbc.idk.ir.convert;
    exports com.github.natanbc.idk.ir.misc;
    exports com.github.natanbc.idk.ir.operation;
    exports com.github.natanbc.idk.ir.value;
    exports com.github.natanbc.idk.ir.variable;
    
    requires transitive idk.ast;
    requires transitive idk.common;
}
