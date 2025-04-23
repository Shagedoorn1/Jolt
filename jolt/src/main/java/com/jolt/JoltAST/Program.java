package com.jolt.JoltAST;

import java.util.List;
public class Program {
    public final java.util.List<Stmt> statements;
    
    public Program(java.util.List<Stmt> statements) {this.statements = statements;}
    public List<Stmt> getStatements() {return statements;}

    @Override
    public String toString() {
        return "Program(" + statements + ")";
    }
}