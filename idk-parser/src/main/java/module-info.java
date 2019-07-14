module idk.parser {
    exports com.github.natanbc.idk.parser;
    
    requires transitive com.github.natanbc.pratt;
    requires transitive jsr305;
    
    requires transitive idk.ast;
    requires transitive idk.common;
}
