package com.infilos.hints;

import com.infilos.hints.utils.Strings;

import java.util.*;

public final class Configure {
    private boolean enabledAllPinyin = false;
    private boolean enabledAllCapital = false;
    private final Set<String> enabledPinyinFields = new HashSet<>();
    private final Set<String> enabledCapitalFields = new HashSet<>();
    private Evaluator<?> evaluator;

    private Configure() {
    }

    public static Configure builder() {
        return new Configure();
    }

    public Configure enableAllPinyin() {
        this.enabledAllPinyin = true;
        return this;
    }

    public Configure enableAllCapital() {
        this.enabledAllCapital = true;
        return this;
    }

    public Configure enablePinyin(String field) {
        if (Strings.nonBlank(field)) {
            enabledPinyinFields.add(field);
        }

        return this;
    }

    public Configure enableCapital(String field) {
        if (Strings.nonBlank(field)) {
            enabledCapitalFields.add(field);
        }
        return this;
    }

    public Configure withEvaluator(Evaluator<?> evaluator) {
        this.evaluator = evaluator;
        return this;
    }

    public Configure build() {
        return this;
    }

    boolean isPinyinEnabled(String field) {
        return enabledAllPinyin || enabledPinyinFields.contains(field);
    }

    boolean isCapitalEnabled(String field) {
        return enabledAllCapital || enabledCapitalFields.contains(field);
    }

    Optional<Evaluator<?>> evaluator() {
        return Optional.ofNullable(evaluator);
    }
}
