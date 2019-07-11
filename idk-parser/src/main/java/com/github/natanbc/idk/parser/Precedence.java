package com.github.natanbc.idk.parser;

public class Precedence {
    public static final int ASSIGNMENT = 1;
    public static final int DISJUNCTION = 2; // a || b
    public static final int CONJUNCTION = 3; // a && b
    public static final int CONDITIONAL = 4;
    public static final int SUM = 5;
    public static final int PRODUCT = 6;
    public static final int EXPONENT = 7;
    public static final int PREFIX = 8;
    public static final int POSTFIX = 9;
    public static final int CALL = 10;
    public static final int MEMBER_ACCESS = 11;
}
