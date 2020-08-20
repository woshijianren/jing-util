package com.util;

public enum Operator {
    EQ("="),
    GT(">"),
    LT("<"),
    GTE(">="),
    LTE("<="),
    N("<>"),
    LIKE("LIKE"),
    NLIKE("NOT LIKE"),
    IS("IS"),
    ISN("IS NOT"),
    IN("IN"),
    NIN("NOT IN"),
    BETWEEN("BETWEEN"),
    NBETWEEN("NOT BETWEEN"),
    STARTWITH("LIKE%"),
    ENDWITH("%LIKE"),
    CONTAINS("@>"),
    NCONTAINS("!@>"),
    CONTAIN_ANY("&&");

    public String operator;

    private Operator(String operator) {
        this.operator = operator;
    }

    public static String findSQLOperator(String operator) throws RuntimeException {
        try {
            String opear = valueOf(operator).operator;
            return opear;
        } catch (IllegalArgumentException var3) {
            throw new RuntimeException(operator + " is not support!");
        }
    }
}
