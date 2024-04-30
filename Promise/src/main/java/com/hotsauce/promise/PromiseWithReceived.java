package com.hotsauce.promise;

public abstract class PromiseReceived<T,T1> extends Promise<T1> {
    protected abstract void onPromiseResolved(T result);
    @Override
    protected void onPromise(){}
}
