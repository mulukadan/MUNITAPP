package com.munit.m_unitapp.DB;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.munit.m_unitapp.MODELS.DailySales;
import com.munit.m_unitapp.TOOLS.Constants;

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
