package com.jolt.JoltAST;

import java.util.List;

public class CallExpr extends Expr {
    private final Expr callee;
    private final List<Expr> arguments;

    public CallExpr(Expr callee, List<Expr> arguments) {
        this.callee = callee;
        this.arguments = arguments;
    }

    public Expr getCallee(){return callee;}
    public List<Expr> getArguments(){return arguments;}
}
