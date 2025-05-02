package com.jolt.JoltAST;

import java.util.List;

public class TensorLiteral extends Expr {
    private final List<?> elements;

    public TensorLiteral(List<?> elements) {
        this.elements = elements;
    }

    public List<?> getElements() {
        return elements;
    }
}
