package com.infilos.hints;

import javax.annotation.Nullable;

public class Attribute {
    private final String origin;
    private final String pinyin;
    private final String capital;

    Attribute(String origin, String pinyin, String capital) {
        this.origin = origin;
        this.pinyin = pinyin;
        this.capital = capital;
    }

    @Nullable
    public String origin() {
        return origin;
    }

    @Nullable
    public String pinyin() {
        return pinyin;
    }

    @Nullable
    public String capital() {
        return capital;
    }
}
