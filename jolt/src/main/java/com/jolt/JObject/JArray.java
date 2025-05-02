package com.jolt.JObject;

import java.util.Arrays;

public class JArray {
    private final double[] elements;

    public JArray(double[] elements) {
        this.elements = elements;
    }

    public double get(int index) {
        return elements[index];
    }

    public int length() {
        return elements.length;
    }

    public JArray add(JArray other) {
        if (this.length() != other.length()) {
            throw new RuntimeException("Array sizes must match.");
        }
        double[] result = new double[elements.length];
        for (int i = 0; i < elements.length; i++) {
            result[i] = elements[i] + other.elements[i];
        }
        return new JArray(result);
    }
    public JArray sub(JArray other) {
        if (this.length() != other.length()) {
            throw new RuntimeException("Array sizes must match.");
        }
        double[] result = new double[elements.length];
        for (int i = 0; i < elements.length; i++) {
            result[i] = elements[i] - other.elements[i];
        }
        return new JArray(result);
    }
    public JArray mul(JArray other) {
        if (this.length() != other.length()) {
            throw new RuntimeException("Array sizes must match.");
        }
        double[] result = new double[elements.length];
        for (int i = 0; i < elements.length; i++) {
            result[i] = elements[i] * other.elements[i];
        }
        return new JArray(result);
    }
    public JArray div(JArray other) {
        if (this.length() != other.length()) {
            throw new RuntimeException("Array sizes must match.");
        }
        double[] result = new double[elements.length];
        for (int i = 0; i < elements.length; i++) {
            result[i] = elements[i] / other.elements[i];
        }
        return new JArray(result);
    }
    public JArray scalarAdd(double other) {
        double[] result = new double[elements.length];
        for (int i = 0; i < elements.length; i++) {
            result[i] = elements[i] + other;
        }
        return new JArray(result);
    }
    public JArray scalarSub(double other) {
        double[] result = new double[elements.length];
        for (int i = 0; i < elements.length; i++) {
            result[i] = elements[i] - other;
        }
        return new JArray(result);
    }
    public JArray scalarMult(double other) {
        double[] result = new double[elements.length];
        for (int i = 0; i < elements.length; i++) {
            result[i] = elements[i] * other;
        }
        return new JArray(result);
    }
    public JArray scalarDiv(double other) {
        double[] result = new double[elements.length];
        for (int i = 0; i < elements.length; i++) {
            result[i] = elements[i] / other;
        }
        return new JArray(result);
    }
    @Override
    public String toString() {
        return Arrays.toString(elements);
    }
}
