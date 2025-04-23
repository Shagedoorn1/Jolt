package com.jolt.JoltAST;

public class AssignStmt extends Stmt {
    public final String name;
    public final Expr value;
    
    public AssignStmt(String name, Expr value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return "AssignStmt(" + name + " = " + value + ")";
    }
}