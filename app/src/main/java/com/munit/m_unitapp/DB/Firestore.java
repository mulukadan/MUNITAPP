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
import com.munit.m_unitapp.TOOLS.Constants;

import java.util.HashMap;
import java.util.Map;

public class Firestore {
    FirebaseFirestore firedb;
    Context mcontext;

    String dailysalesPath = Constants.dailySalesPath;

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
}
