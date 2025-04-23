package com.jolt.JoltInterpreter;

import com.jolt.JoltAST.*;
public class Interpreter {
    private final Environment globals = new Environment();

    public void interpret(Program program) {
        for (Stmt stmt : program.getStatements()) {
            execute(stmt);
        }
    }

    private void execute(Stmt stmt) {
        if (stmt instanceof VarDecl decl) {
            Object value = evaluate(decl.getValue());
            globals.define(decl.getName(), value);
        } else if (stmt instanceof PrintStmt print) {
            Object value = evaluate(print.getExpr());
            System.out.println(value);
        } else if (stmt instanceof IfStmt ifStmt) {
            if (truthy(evaluate(ifStmt.getCondition()))) {
                for (Stmt s : ifStmt.getThenBranch()) execute(s);
            } else {
                for (Stmt s : ifStmt.getElseBranch()) execute(s);
            }
        } else if (stmt instanceof WhileStmt whileStmt) {
            while (truthy(evaluate(whileStmt.getCondition()))) {
                for (Stmt s : whileStmt.getBody()) execute(s);
            }
        } else {
            throw new RuntimeException("Unknown statement type: " + stmt);
        }
    }

    private Object evaluate(Expr expr) {
        if (expr instanceof Literal lit) {
            return lit.getValue();
        }
        if (expr instanceof Identifier ident) {
            return globals.get(ident.getName());
        }
        if (expr instanceof BinaryExpr bin) {
            Object left = evaluate(bin.getLeft());
            Object right = evaluate(bin.getRight());
            return switch (bin.getOperator()) {
                case "+" -> (double) left + (double) right;
                case "-" -> (double) left - (double) right;
                case "*" -> (double) left * (double) right;
                case "/" -> (double) left / (double) right;
                case "==" -> left.equals(right);
                case "!=" -> !left.equals(right);
                case ">" -> (double) left > (double) right;
                case "<" -> (double) left < (double) right;
                case ">=" -> (double) left >= (double) right;
                case "<=" -> (double) left <= (double) right;
                default -> throw new RuntimeException("Unknown operator: " + bin.getOperator());
            };
        }
        throw new RuntimeException("Unknown expression: " + expr);
    }

    private boolean truthy(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean b) return b;
        if (value instanceof Double d) return d != 0;
        return true;
    }
}