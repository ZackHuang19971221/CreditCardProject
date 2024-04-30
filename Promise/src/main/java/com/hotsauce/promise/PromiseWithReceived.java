package com.hotsauce.promise;

public abstract class PromiseWithReceived<T,T1> extends PromiseBase<T1> {
    protected abstract void onPromise(T result);
}
