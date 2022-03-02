package com.ariel.tremp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

@Entity
public class User {
    public final static String LAST_UPDATED = "LAST_UPDATED";

    @PrimaryKey
    @NonNull
    private String userEmail;
    private String userFirstName;
    private String userLastName;
    private String userPhone;
    private String userPassword;
    private String uid;

    Long lastUpdated = new Long(0);

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    public Long getLastUpdated() {
        return lastUpdated;
    }

    public User() { }

    public User(String uid, String uEmail,String uFirstName,String uLastName,String uPassword, String uPhone) {
        this.uid = uid;
        this.userEmail = uEmail;
        this.userFirstName = uFirstName;
        this.userLastName = uLastName;
        this.userPassword = uPassword;
        this.userPhone = uPhone;
    }

    @NonNull
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(@NonNull String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Map<String,Object> toJson(){
        Map<String, Object> json = new HashMap<>();
        json.put("email", getUserEmail());
        json.put("firstname", getUserFirstName());
        json.put("lastname", getUserLastName());
        json.put("phone", getUserPhone());
        json.put("uid", getUid());
        json.put("password", getUserPassword());//check if it correct to store the password in the user doc
        json.put(LAST_UPDATED, FieldValue.serverTimestamp());

        return json;
    }
    //convert from json Map to User object
    public static User fromJson(Map<String,Object> json){
        String email = (String)json.get("email");
        String firstName = (String)json.get("firstname");
        String lastName = (String)json.get("lastname");
        String phone = (String)json.get("phone");
        String password = (String)json.get("password");
        String uid = (String) json.get("uid");
        User user = new User(uid, email,firstName,lastName,password, phone);
        Timestamp ts = (Timestamp)json.get(LAST_UPDATED);
        user.setLastUpdated(ts.getSeconds());
        return user;
    }

    public static Long getLocalLastUpdated(){
        Long localLastUpdate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .getLong("USER_LAST_UPDATE",0);
        return localLastUpdate;
    }

    public static void setLocalLastUpdated(Long date){
        SharedPreferences.Editor editor = MyApplication.getContext()
                .getSharedPreferences("TAG", Context.MODE_PRIVATE).edit();
        editor.putLong("USER_LAST_UPDATE",date);
        editor.commit();
        Log.d("TAG", "new lud " + date);
    }

    @Override
    public String toString() {
        return "User{" +
                "userEmail='" + userEmail + '\'' +
                ", userFirstName='" + userFirstName + '\'' +
                ", userLastName='" + userLastName + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", uid='" + uid + '\'' +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}