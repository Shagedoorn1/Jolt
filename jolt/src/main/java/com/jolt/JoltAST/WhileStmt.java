package com.jolt.JoltAST;

import java.util.List;
public class WhileStmt extends Stmt {
    public final Expr condition;
    public final java.util.List<Stmt> body;
    
    public WhileStmt(Expr condition, List<Stmt> body) {
        this.condition = condition;
        this.body = body;
    }
    
    public Expr getCondition(){return condition;}
    public List<Stmt> getBody(){return body;}

    @Override
    public String toString() {
        return "WhileStmt(" + condition + ", " + body + ")";
    }
}