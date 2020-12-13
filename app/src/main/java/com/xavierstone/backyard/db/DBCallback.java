package com.xavierstone.backyard.db;

public interface DBCallback<T> {
    void onComplete(Result<T> result);
}
