package com.xavierstone.backyard.db;

import android.os.Handler;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.xavierstone.backyard.BackyardApplication;
import com.xavierstone.backyard.models.User;

import java.util.concurrent.Executor;

public class LoginRepository {
    private final DBHandler db = BackyardApplication.getDB();
    private final Executor executor;
    private final Handler resultHandler;
    private final MutableLiveData<Result<User>> resultLiveData;

    public LoginRepository(Executor executor, Handler resultHandler, LifecycleOwner owner, Observer<Result<User>> resultObserver){
        this.executor = executor;
        this.resultHandler = resultHandler;
        resultLiveData = new MutableLiveData<>();
        resultLiveData.observe(owner, resultObserver);
    }

    //TODO: implement View Model class
    // for now, just have a public method
    public void signIn(final String email, final String password){
        signIn(email, password, new DBCallback<User>() {
            @Override
            public void onComplete(Result<User> result) {
                resultLiveData.postValue(result);
            }
        });
    }

    // Sign In
    // TODO: correctly implement sign in
    public void signIn(final String email, final String password, final DBCallback<User> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    User result = db.validateUser(email, password);
                    notifyResult(new Result.Success<User>(result), callback);
                }catch (Exception e) {
                    notifyResult(new Result.Error<User>(e), callback);
                }
            }
        });
    }

    private void notifyResult(
            final Result<User> result,
            final DBCallback<User> callback
    ){
        resultHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onComplete(result);
            }
        });
    }

    public User createAccount(String name, String email, String password){
        return db.createAccount(name, email, password);
    }
}
