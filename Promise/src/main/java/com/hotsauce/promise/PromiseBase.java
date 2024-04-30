package com.hotsauce.promise;

public abstract class PromiseBase<T> {
    PromiseBase() {

    }
    void reject();
    void promise();
    protected void resolve(T data);
}
