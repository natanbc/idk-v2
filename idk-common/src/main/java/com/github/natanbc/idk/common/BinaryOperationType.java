package com.github.natanbc.idk.common;

public enum BinaryOperationType {
    ADD, SUB, MUL, DIV, MOD, POW, EQ, NEQ, GREATER,
    GREATER_EQ, SMALLER, SMALLER_EQ, AND, OR;
    
    private final String titleCase;
    
    BinaryOperationType() {
        var sb = new StringBuilder(name().length());
        var first = true;
        for(var c : name().toCharArray()) {
            if(c == '_') {
                first = true;
            } else {
                if(first) {
                    sb.append(Character.toUpperCase(c));
                } else {
                    sb.append(Character.toLowerCase(c));
                }
                first = false;
            }
        }
        this.titleCase = sb.toString();
    }
    
    public String getTitleCase() {
        return titleCase;
    }
}
