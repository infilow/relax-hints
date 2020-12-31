package com.infilos.hints;

public interface Evaluator<T> {

    T evaluate(String attribute, String word);
    
    boolean check(T t);

    int compare(T t1, T t2);
}
