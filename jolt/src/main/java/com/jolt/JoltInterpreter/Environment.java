package com.jolt.JoltInterpreter;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Object> values = new HashMap<>();

    public void define(String name, Object value) {
        values.put(name, value);
    }

    public Object get(String name) {
        if (values.containsKey(name)) {
            return values.get(name);
        }
        throw new RuntimeException("Undefined variable: " + name);
    }
}
