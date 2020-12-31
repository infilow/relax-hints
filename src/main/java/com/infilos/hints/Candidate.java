package com.infilos.hints;

import javax.annotation.Nonnull;
import java.util.Map;

public class Candidate<T> {
    private final T value;
    private final Map<String, Attribute> attributes;

    Candidate(T value, Map<String, Attribute> attributes) {
        this.value = value;
        this.attributes = attributes;
    }

    @Nonnull
    public T value() {
        return value;
    }

    @Nonnull
    public Map<String, Attribute> attributes() {
        return attributes;
    }

    public boolean hasField(String name) {
        return attributes.containsKey(name);
    }

    public Attribute fieldAttribute(String name) {
        return attributes.get(name);
    }
}
