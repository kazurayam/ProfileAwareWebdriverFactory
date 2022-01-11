package com.kazurayam.webdriverfactory

class PreferencesModifierBase implements PreferencesModifier {

    private Type type
    private Closure closure

    PreferencesModifierBase(Type type, Closure closure) {
        this.type = type
        this.closure = closure
    }
    @Override
    Type getType() {
        return this.type
    }
    @Override
    Map<String, Object> modify(Map<String, Object> preferences) {
        Objects.requireNonNull(preferences)
        return (Map)closure.call(preferences)
    }
}

