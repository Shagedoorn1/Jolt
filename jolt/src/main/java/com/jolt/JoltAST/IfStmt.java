package com.jolt.JoltAST;

import java.util.List;
public class IfStmt extends Stmt {
    public final Expr condition;
    public final java.util.List<Stmt> thenBranch;
    public final java.util.List<Stmt> elseBranch;
    
    public IfStmt(Expr condition, java.util.List<Stmt> thenBranch, java.util.List<Stmt> elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
    public Expr getCondition(){return condition;}
    public List<Stmt> getThenBranch(){return thenBranch;}
    public List<Stmt> getElseBranch(){ return elseBranch;}

    @Override
    public String toString() {
        return "IfStmt(" + condition + ", " + thenBranch + ", " + elseBranch + ")";
    }
}