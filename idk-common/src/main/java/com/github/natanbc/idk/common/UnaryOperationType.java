package com.github.natanbc.idk.common;

public enum UnaryOperationType {
    NEG, NEGATE;
    
    private final String titleCase;
    
    UnaryOperationType() {
        this.titleCase = name().charAt(0) + name().toLowerCase().substring(1);
    }
    
    public String getTitleCase() {
        return titleCase;
    }
}
