package com.jolt.JoltAST;

import java.util.List;

public class ArrayLiteral extends Expr{
    private final List<Expr> elements;

    public ArrayLiteral(List<Expr> elements) {this.elements = elements;}
    public List<Expr> getElements(){return elements;}
}
