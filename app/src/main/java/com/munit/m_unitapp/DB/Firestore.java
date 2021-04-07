package com.munit.m_unitapp.DB;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.munit.m_unitapp.MODELS.DailySales;

import java.util.HashMap;
import java.util.Map;

public class Firestore {
    FirebaseFirestore firedb;
    Context mcontext;

    String dailysalesPath = "dailysales";

    public Firestore(Context mcontext) {
        firedb = FirebaseFirestore.getInstance();
        this.mcontext = mcontext;
    }


    public void addDailySale(DailySales dailySales) {
        String docId = dailySales.getDate().replace("/", "-") + ":" + dailySales.getUserId();
        firedb.collection(dailysalesPath).document(docId)
                .set(dailySales)
                .addOnSuccessListener(aVoid -> {
                    // Success
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(mcontext, "Error adding document", Toast.LENGTH_SHORT).show();
                });


    }

    public DailySales getDailySale(String date, String userId) {
        final DailySales[] dailySales = {new DailySales()};
        String docId = date.replace("/", "-") + ":" + userId;

        DocumentReference docRef = firedb.collection(dailysalesPath).document(docId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        dailySales[0] = document.toObject(DailySales.class);
                    } else {
//                        Log.d(TAG, "No such document");
                    }
                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        return dailySales[0];
    }


    public void fireStoreTest() {
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Ada");
        user.put("last", "Lovelace");
        user.put("born", 1815);

// Add a new document with a generated ID
        firedb.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(mcontext, "DocumentSnapshot added with ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mcontext, "Error adding document", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void readfireStore() {
        firedb.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Toast.makeText(mcontext, document.getId() + " => " + document.getData(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
                            Toast.makeText(mcontext, "Error getting documents.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
