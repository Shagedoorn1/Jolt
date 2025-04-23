package com.jolt.JoltAST;

public class BinaryExpr extends Expr {
    public final Expr left;
    public final String op;
    public final Expr right;
    
    public BinaryExpr(Expr left, String op, Expr right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }
    
    public String getOperator(){return op;}
    public Expr getLeft(){return left;}
    public Expr getRight(){return right;}

    @Override
    public String toString() {
        return "BinaryExpr(" + left + " " + op + " " + right + ")";
    }
}