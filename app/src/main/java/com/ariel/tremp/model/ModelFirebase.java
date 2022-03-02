package com.ariel.tremp.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.ariel.tremp.GetUser;
import com.ariel.tremp.OnComplete;
import com.ariel.tremp.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.LinkedList;

//user document - Contains all personal details about single user
// ,The ID of each user doc is the user's email
//users collection - Contains all user documents
public class ModelFirebase {
    private static final String TAG = "ModelFirebase TAG";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ModelFirebase(){
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

//    public void signUp(String email, String password, String firstName, String lastName, OnComplete onComplete) {
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//
//        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                FirebaseUser firebaseUser = mAuth.getCurrentUser();
//                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                String uid = firebaseUser.getUid();
//
//                User user = new User(uid, email, firstName, lastName, password);
//
//                db.collection("users")
//                        .document(uid)
//                        .set(user.toJson())
//                        .addOnSuccessListener(documentReference -> onComplete.handleAfter())
//                        .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
//            }
//            else onComplete.handleError();
//        });
//    }

   //convert from users collection to users list(linked list)
    public void getAllUsers(Long since, Model.GetAllUsersListener listener) {
        db.collection("users").whereGreaterThanOrEqualTo(User.LAST_UPDATED,new Timestamp(since, 0)).get().addOnCompleteListener(task -> {
                    LinkedList<User> usersList = new LinkedList<User>();
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot doc: task.getResult()){
                            User u = User.fromJson(doc.getData());
                            if (u != null) {
                                usersList.add(u);
                            }
                        }
                    }else{

                    }
                    listener.onComplete(usersList);
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onComplete(null);
            }
        });

    }

    public void addUserToFirebaseDB(User user, OnComplete addUser) {
        db.collection("users").document(user.getUid()).set(user.toJson()).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                addUser.handleAfter();
            else
                addUser.handleError();
        });
    }

    public void getUserByUID(String uid, GetUser getUser) {
        db.collection("users").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot ds = task.getResult();
                User user = User.fromJson(ds.getData());
                getUser.handleAfter(user);
            }
            else {
                Log.w(TAG, "Error getting documents.", task.getException());
                getUser.handleError();
            }
        });
    }
}


