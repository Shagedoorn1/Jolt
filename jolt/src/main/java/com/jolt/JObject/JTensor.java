package com.jolt.JObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jolt.JoltAST.ArrayLiteral;
import com.jolt.JoltAST.Expr;
import com.jolt.JoltAST.Literal;

public class JTensor {
    private final Object data;
    private final int[] shape;

    public JTensor(Object data, int[] shape) {
        this.data = data;
        this.shape = shape;
    }

    public static JTensor fromArrayLiteral(ArrayLiteral array){
        Object data = parse(array);
        int[] shape = computeShape(data);
        return new JTensor(data, shape);
    }

    private static Object parse(ArrayLiteral array){
        List<Object> parsed = new ArrayList<>();
        for (Expr e : array.getElements()) {
            if (e instanceof ArrayLiteral nested) {
                parsed.add(parse(nested));
            } else if (e instanceof Literal lit) {
                parsed.add((double) lit.getValue());
            } else {
                throw new RuntimeException("Invalid tensor element: " + e);
            }
        }
        return parsed;
    }
    private static int[] computeShape(Object data) {
        List<Integer> dims = new ArrayList<>();
        Object current = data;
        while (current instanceof List) {
            dims.add(((List<?>) current).size());
            current = ((List<?>) current).get(0);
        }
        return dims.stream().mapToInt(i -> i).toArray();
    }
    public Object getRawData() {return data;}
    public int[] getShape(){return shape;}

    public JTensor add(JTensor other) {
        if (!Arrays.equals(this.shape, other.shape)) {
            throw new RuntimeException("Tensor shape mismatch.");
        }

        Object newData = elementwiseOp(this.data, other.data, Double::sum);
        return new JTensor(newData, this.shape);
    }
    public JTensor sub(JTensor other) {
        if (!Arrays.equals(this.shape, other.shape)) {
            throw new RuntimeException("Tensor shape mismatch.");
        }

        Object newData = elementwiseOp(this.data, other.data, (a,b) -> a - b);
        return new JTensor(newData, this.shape);
    }

    @Override
    public String toString() {
        return data.toString();
    }

    @SuppressWarnings("unchecked")
    private Object elementwiseOp(Object a, Object b, java.util.function.BiFunction<Double, Double, Double> op) {
        if (a instanceof List && b instanceof List) {
            List<Object> listA = (List<Object>) a;
            List<Object> listb = (List<Object>) b;
            List<Object> result = new ArrayList<>();
            for (int i = 0; i < listA.size(); i++) {
                result.add(elementwiseOp(listA.get(i), listb.get(i), op));
            }
            return result;
        } else if (a instanceof Double && b instanceof Double) {
            return op.apply((Double) a, (Double) b);
        } else {
            throw new RuntimeException("Element mismatch: " + a + " vs " + b);
        }
    }
}
