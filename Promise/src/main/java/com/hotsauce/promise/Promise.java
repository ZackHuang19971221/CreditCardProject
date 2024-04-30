package com.hotsauce.promise;

public abstract class Promise<T> extends PromiseBase<T> {
    protected abstract void onPromise();
}
