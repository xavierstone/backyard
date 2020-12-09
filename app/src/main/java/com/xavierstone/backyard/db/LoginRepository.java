package com.xavierstone.backyard.db;

import android.os.Handler;
import android.util.Log;

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
    private final MutableLiveData<Result<LoginResponse>> resultLiveData;

    public LoginRepository(Executor executor, Handler resultHandler, LifecycleOwner owner, Observer<Result<LoginResponse>> resultObserver){
        this.executor = executor;
        this.resultHandler = resultHandler;
        resultLiveData = new MutableLiveData<>();
        resultLiveData.observe(owner, resultObserver);
    }

    //TODO: implement View Model class
    // for now, just have a public method
    public void signIn(final String email, final String password){
        signIn(email, password, new DBCallback<LoginResponse>() {
            @Override
            public void onComplete(Result<LoginResponse> result) {
                resultLiveData.postValue(result);
            }
        });
    }

    // Sign In
    // TODO: correctly implement sign in
    public void signIn(final String email, final String password, final DBCallback<LoginResponse> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    User result = db.validateUser(email, password);
                    LoginResponse response;
                    if (result == null)
                        response = new LoginResponse(false);
                    else
                        response = new LoginResponse(true, result);
                    notifyResult(new Result.Success<LoginResponse>(response), callback);
                }catch (Exception e) {
                    notifyResult(new Result.Error<LoginResponse>(e), callback);
                }
            }
        });
    }

    private void notifyResult(
            final Result<LoginResponse> result,
            final DBCallback<LoginResponse> callback
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
