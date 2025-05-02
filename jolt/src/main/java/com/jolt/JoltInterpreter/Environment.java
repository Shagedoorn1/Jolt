package com.jolt.JoltInterpreter;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Object> values = new HashMap<>();
    private final Environment parent;

    public Environment() {
        this.parent = null;
    }

    public Environment(Environment parent) {
        this.parent = parent;
    }

    public void define(String name, Object value) {
        values.put(name, value);
    }

    public Object get(String name) {
        if (values.containsKey(name)) {
            return values.get(name);
        } else if (parent != null) {
            return parent.get(name);
        } else {
            throw new RuntimeException("Undefined variable: " + name);
        }
    }
    public Map<String, Object> snapshot() {
        return new HashMap<>(values);
    }
    public void set(String name, Object value) {
        if (values.containsKey(name)) {
            values.put(name, value);
        } else if (parent != null) {
            parent.set(name, value);
        } else {
            throw new RuntimeException("Undefined variable: " + name);
        }
    }

    public Environment copy() {
        Environment copy = new Environment(parent);
        copy.values.putAll(this.values);
        return copy;
    }
    public void printVariables() {
        System.out.println("Environment variables:");
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        if (parent != null) {
            parent.printVariables();  // Recursively print the parent environment's variables.
        }
    }
}