package com.tigerobo.x.pai.engine.auto.ml;

public interface AmlTask<T,R> {

    R run(T t);
}
