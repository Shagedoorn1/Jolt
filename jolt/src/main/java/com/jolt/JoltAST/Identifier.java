package com.jolt.JoltAST;

public class Identifier extends Expr {
    public final String name;

    public Identifier(String name) {this.name = name;}
    public String getName(){return name;}

    @Override
    public String toString() {
        return "Identifier("+ name + ")";
    }
}