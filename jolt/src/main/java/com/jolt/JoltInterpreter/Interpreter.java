package com.jolt.JoltInterpreter;

import java.util.ArrayList;
import java.util.List;

import com.jolt.JObject.JArray;
import com.jolt.JObject.JTensor;
import com.jolt.JoltAST.ArrayAccess;
import com.jolt.JoltAST.ArrayLiteral;
import com.jolt.JoltAST.BinaryExpr;
import com.jolt.JoltAST.CallExpr;
import com.jolt.JoltAST.Expr;
import com.jolt.JoltAST.Identifier;
import com.jolt.JoltAST.IfStmt;
import com.jolt.JoltAST.Literal;
import com.jolt.JoltAST.PrintStmt;
import com.jolt.JoltAST.Program;
import com.jolt.JoltAST.Stmt;
import com.jolt.JoltAST.TensorLiteral;
import com.jolt.JoltAST.VarDecl;
import com.jolt.JoltAST.WhileStmt;

public class Interpreter {
    private final Environment globals = new Environment();
    private Environment environment = globals;

    public void interpret(Program program) {
        for (Stmt stmt : program.getStatements()) {
            execute(stmt);
        }
    }

    private void execute(Stmt stmt) {
        if (stmt instanceof VarDecl decl) {
            Object value = evaluate(decl.getValue());
            environment.define(decl.getName(), value);
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
        if (expr instanceof TensorLiteral tensorLit) {
            Object data = evaluateTensorElements(tensorLit.getElements());
            int[] shape = inferShape(data);
            return new JTensor(data, shape);
        }
        if (expr instanceof CallExpr call) {
            Object callee = evaluate(call.getCallee());

            List<Object> args = new ArrayList<>();
            for (Expr argExpr : call.getArguments()){
                args.add(evaluate(argExpr));
            }
            throw new RuntimeException("Attempted to call a non-function.");
        }
        if (expr instanceof ArrayLiteral arrLit) {
            double[] values = arrLit.getElements().stream().map(this::evaluate).mapToDouble(v -> {
                if (v instanceof  Double d) return d;
                throw new RuntimeException("Arrays must contain only numbers.");
                }).toArray();
            return new JArray(values);
        }
        if (expr instanceof ArrayAccess access) {

            Object arrayObj = evaluate(access.getArray());
            Object indexObj = evaluate(access.getIndex());

            if (!(arrayObj instanceof JArray array)) {
                throw new RuntimeException("Target is not an array.");
            }
            if (!(indexObj instanceof Double indexDouble)) {
                throw new RuntimeException("Array index must be a number");
            }
            int index = indexDouble.intValue();
            if (index < 0 || index >= array.length()) {
                throw new RuntimeException("Array index out of bounds");
            }
            return array.get(index);
        }
        if (expr instanceof Literal lit) {
            return lit.getValue();
        }
        if (expr instanceof Identifier ident) {
            return globals.get(ident.getName());
        }
        if (expr instanceof BinaryExpr bin) {

            Object left = evaluate(bin.getLeft());
            Object right = evaluate(bin.getRight());
            if (left instanceof Double && right instanceof JArray){
                List<Object> nargs = rectify(left, right);
                left = nargs.get(0);
                right = nargs.get(1);
            }
            if (left instanceof JArray && right instanceof JArray) {
                return switch (bin.getOperator()) {
                    case "+" -> ((JArray) left).add((JArray) right);
                    case "-" -> ((JArray) left).sub((JArray) right);
                    case "*" -> ((JArray) left).mul((JArray) right);
                    case "/" -> ((JArray) left).div((JArray) right);
                    default -> throw new RuntimeException("Unsupported operator for arrays: "+bin.getOperator());
                };
            }
            if (left instanceof JTensor && right instanceof JTensor) {
                return switch (bin.getOperator()) {
                    case "+" -> ((JTensor) left).add((JTensor) right);
                    case "-" -> ((JTensor) left).sub((JTensor) right);
                    default -> throw new RuntimeException("Unsupported operator for tensors: " + bin.getOperator());
                };
            }
            if (left instanceof JArray && right instanceof Double) {
                return switch (bin.getOperator()) {
                    case "+" -> ((JArray) left).scalarAdd((double) right);
                    case "-" -> ((JArray) left).scalarSub((double) right);
                    case "*" -> ((JArray) left).scalarMult((double) right);
                    case "/" -> ((JArray) left).scalarDiv((double) right);
                    default -> throw new RuntimeException("Unsupported operator for Arrays-scalar: " + bin.getOperator());
                };
            }
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
    private List<Object> rectify(Object left, Object right) {
        List<Object> nargs = new ArrayList<>();
        Object temp = right;
        Object nright = left;
        Object nleft = temp;
        nargs.add(nleft);
        nargs.add(nright);
        return nargs;
    }
    private boolean truthy(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean b) return b;
        if (value instanceof Double d) return d != 0;
        return true;
    }
    
    @SuppressWarnings("Unchecked")
    private Object evaluateTensorElements(List<?> exprList) {
        if (!exprList.isEmpty() && exprList.get(0) instanceof Expr) {
            List<Double> row = new ArrayList<>();
            for (Object o : exprList) {
                Object val = evaluate((Expr) o);
                if (!(val instanceof Double d)) {
                    throw new RuntimeException("Tensor elements must be numeric.");
                }
                row.add(d);
            }
            return row;
        }
        List<Object> nested = new ArrayList<>();
        for (Object o : exprList) {
            nested.add(evaluateTensorElements((List<?>) o));
        }
        return nested;
    }
    public Object evaluateWithScope(Expr expr, Environment local) {
        Environment previous = this.globals;
        this.environment = local;
        try {
            return evaluate(expr);
        } finally {
            this.environment = previous;
        }
    }
    private int[] inferShape(Object data) {
        List<Integer> shape = new ArrayList<>();
        Object current = data;

        while (current instanceof List) {
            List<?> list = (List<?>) current;
            shape.add(list.size());
            current = list.isEmpty() ? null : list.get(0);
        }
        return shape.stream().mapToInt(i -> i).toArray();
    }
}