package com.hotsauce.promise;

public interface IResolve<T> {
    void onPromiseResolve(T data);
}
