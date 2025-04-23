package com.jolt.JoltAST;

public class PrintStmt extends Stmt {
    public final Expr value;
    
    public PrintStmt(Expr value) {this.value = value;}
    public Expr getExpr() {return value;}

    @Override
    public String toString() {
        return "PrintStmt(" + value + ")";
    }
}