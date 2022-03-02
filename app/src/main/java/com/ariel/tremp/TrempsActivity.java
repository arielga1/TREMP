package com.ariel.tremp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class TrempsActivity extends AppCompatActivity {

    ArrayList<Tremp> tremps = new ArrayList<>(); // list of all tremps
    RecyclerView recyclerView;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tremps);

        recyclerView = findViewById(R.id.trempsList);

        TrempAdapter adapter = new TrempAdapter(tremps, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // get all tremps data from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("tremps").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot ds : task.getResult().getDocuments()) {
                    // add all tremps to the list
                    tremps.add(ds.toObject(Tremp.class));
                }

                adapter.notifyDataSetChanged();
                Log.i("TAG", "onCreate: " + tremps);
            }
        });
    }
}