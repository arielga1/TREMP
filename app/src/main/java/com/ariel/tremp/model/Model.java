package com.ariel.tremp.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ariel.tremp.GetUser;
import com.ariel.tremp.MyApplication;
import com.ariel.tremp.OnComplete;
import com.ariel.tremp.User;

import java.util.List;

public class Model {
    public static ModelFirebase modelFirebase = new ModelFirebase();
    public static MutableLiveData<List<User>> usersListLd = new MutableLiveData<>();
    public interface GetAllUsersListener{
        void onComplete(List<User> data);
    }

    private static void reloadUsersList() {
        //1. get local last update
        Long localLastUpdate = User.getLocalLastUpdated();
        Log.d("TAG","localLastUpdate: " + localLastUpdate);
        //2. get all users record since local last update from firebase
        modelFirebase.getAllUsers(localLastUpdate,(list)-> MyApplication.executorService.execute(()->{
            //3. update local last update date
            //4. add new records to the local db
            Long lLastUpdate = new Long(0);
            Log.d("TAG", "FB returned " + list.size());
            for(User u : list){
                AppLocalDB.db.userDao().insertAll(u);
                if (u.getLastUpdated() > lLastUpdate){
                    lLastUpdate = u.getLastUpdated();
                }
            }
            User.setLocalLastUpdated(lLastUpdate);

            //5. return all records to the caller
            List<User> usersList = AppLocalDB.db.userDao().getAll();
            usersListLd.postValue(usersList);
        }));
    }

    public static LiveData<List<User>> getAll(){
        return usersListLd;
    }

    public static void addUserToFirebaseDB(User user, OnComplete listener) {
        modelFirebase.addUserToFirebaseDB(user, new OnComplete() {
            @Override
            public void handleAfter() {
                reloadUsersList();
                listener.handleAfter();
                MyApplication.executorService.execute(() -> {
                    AppLocalDB.db.userDao().insertAll(user);
                    MyApplication.mainHandler.post(listener::handleAfter);
                });
            }

            @Override
            public void handleError() {

            }
        });
    }

    public static void getUserByUID(String uid, GetUser getUser) {
        modelFirebase.getUserByUID(uid, getUser);
    }
}
