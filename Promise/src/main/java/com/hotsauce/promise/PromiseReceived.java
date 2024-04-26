package com.hotsauce.promise;

public abstract class PromiseReceived<T,T1> {
    protected abstract void onPromiseResolved(T result);
    protected abstract void onPromiseRejected(T result);

    private Promise<T1> promise;
    void setPromise(Promise<T1> value) {
        promise = value;
    }
    Promise<T1> getPromise(){
        return promise;
    }

    protected void resolve(T1 data) {
        getPromise().resolve(data);
    }

    protected void reject(T1 data) {
        getPromise().reject(data);
    }
}
