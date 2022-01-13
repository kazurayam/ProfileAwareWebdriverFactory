package com.kazurayam.webdriverfactory;

import groovy.lang.Closure;

import java.util.Map;
import java.util.Objects;

public class PreferencesModifierBase implements PreferencesModifier {
    public PreferencesModifierBase(Type type, Closure closure) {
        this.type = type;
        this.closure = closure;
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public Map<String, Object> modify(Map<String, Object> preferences) {
        Objects.requireNonNull(preferences);
        return (Map) closure.call(preferences);
    }

    private Type type;
    private Closure closure;
}
