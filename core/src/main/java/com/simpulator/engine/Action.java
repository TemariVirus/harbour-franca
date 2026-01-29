package com.simpulator.engine;

public interface Action<T> {
    public void act(float deltaTime, T extraData);
}
