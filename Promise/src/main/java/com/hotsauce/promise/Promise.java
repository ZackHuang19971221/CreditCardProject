package com.hotsauce.promise;

public abstract class Promise<T> {
    private Status status;
    private T result;

    public Promise() {
        this.status = Status.PENDING;
    }

    protected abstract void onPromise() throws Exception;

    protected void resolve(T data) {
        if(getStatus() != Status.PENDING) {return;}
        result = data;
        setStatus(Status.FULFILLED);
    }
    protected void reject(T data) {
        if(getStatus() != Status.PENDING) {return;}
        result = data;
        setStatus(Status.REJECTED);
    }

    protected void onException(Exception exception) {}

    protected void onFinally() {}

    public Status getStatus() {
        return status;
    }
    private void setStatus(Status value) {
        if(getStatus() == Status.PENDING) {
            status = value;
        }
    }

    public T getResult() {
        return result;
    }

    public <T1> Promise<T1> promiseThen(PromiseReceived<T,T1> promiseReceived) {
        Promise<T1> promise = new Promise<>() {
            @Override
            protected void onPromise() throws Exception {}
        };
        promiseReceived.setPromise(promise);
        if(getStatus() == Status.PENDING) {
            try {
                onPromise();
            }catch (Exception exception) {
                onException(exception);
            }finally {
                onFinally();
            }
        }
        if(getStatus() == Status.FULFILLED) {
            promiseReceived.onPromiseResolved(getResult());
        }
        if(getStatus() == Status.REJECTED) {
            promiseReceived.onPromiseRejected(getResult());
        }
        return promise;
    }

    public enum Status {
        PENDING,
        FULFILLED,
        REJECTED
    }
}
