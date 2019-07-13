package com.github.natanbc.idk.parser;

public class Precedence {
    private static int next = 0;
    
    private static int next() {
        next++;
        return next;
    }
    
    public static final int ASSIGNMENT = next();
    public static final int DISJUNCTION = next(); // a || b
    public static final int CONJUNCTION = next(); // a && b
    public static final int CONDITIONAL = next();
    public static final int RANGE = next();
    public static final int SUM = next();
    public static final int PRODUCT = next();
    public static final int EXPONENT = next();
    public static final int PREFIX = next();
    public static final int POSTFIX = next();
    public static final int CALL = next();
    public static final int MEMBER_ACCESS = next();
}
