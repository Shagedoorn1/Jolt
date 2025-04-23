package com.jolt.JoltAST;
public class VarDecl extends Stmt {
    public final String varType;
    public final String name;
    public final Expr value;
    
    public VarDecl(String varType, String name, Expr value) {
        this.varType = varType;
        this.name = name;
        this.value = value;
    }
    public String getVarType() {return varType;}
    public String getName() {return name;}
    public Expr getValue() {return value;}
    
    @Override
    public String toString() {
        return "VarDecl(" + varType + " " + name + " = " + value + ")";
    }
}