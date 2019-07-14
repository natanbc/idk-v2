module idk.compiler {
    exports com.github.natanbc.idk.compiler;
    
    requires com.headius.invokebinder;
    
    requires transitive idk.ir;
    requires transitive idk.runtime;
}
