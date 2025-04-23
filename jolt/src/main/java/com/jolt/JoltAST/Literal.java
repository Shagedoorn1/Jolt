package com.jolt.JoltAST;

public class Literal extends Expr {
    public final Object value;
    public Literal(Object value) {
        this.value = value;
    }
    
    public Object getValue(){return value;}

    @Override
    public String toString() {
        return "Literal("+ value + ")";
    }
}