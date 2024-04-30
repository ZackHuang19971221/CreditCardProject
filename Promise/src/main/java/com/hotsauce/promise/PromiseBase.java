package com.hotsauce.promise;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class PromiseBase<T> {
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private PromiseBase child;
    protected void setChild(PromiseBase child) {
        this.child = child;
    }
    protected PromiseBase getChild() {
        return child;
    }

    private PromiseBase parent;
    protected void setParent(PromiseBase parent) {
        this.parent = parent;
    }
    protected PromiseBase getParent() {
        return parent;
    }

    private PromiseBase.Status status;
    public PromiseBase.Status getStatus() {
        return status;
    }
    private void setStatus(PromiseBase.Status value) {
        if(getStatus() == PromiseBase.Status.PENDING) {
            status = value;
        }
    }

    private T result;
    public T getResult() {
        return result;
    }

    PromiseBase() {
        this.status = Status.PENDING;
    }

    protected void reject() {
        if(getStatus() != Status.PENDING) {return;}
        setStatus(Status.REJECTED);
    }

    protected void resolve(T data) {
        if(getStatus() != Status.PENDING) {return;}
        result = data;
        setStatus(Status.FULFILLED);
    }

    protected void onException(Exception exception) {}

    protected void onFinally() {}

    public <T1> Promise<T1> then(Promise<T1> promise) {
        if(promise != null) {
            child = promise;
            promise.setParent(this);
        }
        return promise;
    }

    public <T1> PromiseWithReceived<T,T1> then(PromiseWithReceived<T,T1> promise) {
        if(promise != null) {
            this.setChild(promise);
            promise.setParent(this);
        }
        return promise;
    }

    public void execute() {
        executor.submit(() -> runFunction(findRoot()));
    }

    private void runFunction(PromiseBase promise) {
        if(promise == null){return;}

        if(promise.getStatus() == Status.PENDING) {
            try {
                if(promise instanceof Promise) {
                    ((Promise<T>) promise).onPromise();
                }
                if(promise instanceof PromiseWithReceived) {
                    ((PromiseWithReceived<T,?>) promise).onPromise((T) promise.parent.getResult());
                }
            } catch (Exception e) {
                setStatus(Status.REJECTED);
                promise.onException(e);
            }
        }

        if(promise.getStatus() == Status.FULFILLED && promise.getChild() != null) {
            runFunction(promise.getChild());
        }else {
            runOnFinally(findLastChild());
        }
    }

    private void runOnFinally(PromiseBase promise) {
        if (promise == null) {
            return;
        }
        promise.onFinally();
        runOnFinally(promise.getParent());
    }

    private PromiseBase findRoot() {
        PromiseBase last = this;
        while (last.getParent() != null && last.getStatus() == Status.PENDING) {
            last = last.getParent();
        }
        return last;
    }

    private PromiseBase findLastChild() {
        PromiseBase last = findRoot();
        while (last.getChild() != null && last.getChild().getStatus() != Status.PENDING) {
            last = last.getChild();
        }
        return last;
    }

    public enum Status {
        PENDING,
        FULFILLED,
        REJECTED
    }
}
