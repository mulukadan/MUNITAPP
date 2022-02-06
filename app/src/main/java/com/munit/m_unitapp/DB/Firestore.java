package com.munit.m_unitapp.DB;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.munit.m_unitapp.MODELS.DailySales;
import com.munit.m_unitapp.MODELS.PoolRecordNew;
import com.munit.m_unitapp.TOOLS.Constants;

public class Firestore {
    FirebaseFirestore firedb;
    Context mcontext;

    String dailysalesPath = Constants.dailySalesPath;
    String poolRecordsPath = Constants.poolRecordsPath;

    public Firestore(Context mcontext) {
        firedb = FirebaseFirestore.getInstance();
        this.mcontext = mcontext;
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firedb.setFirestoreSettings(settings);
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
    public void addPoolRecord(PoolRecordNew poolRecordNew) {
        String docId = poolRecordNew.getDate().replace("/", "-") + ":" + poolRecordNew.getPoolName();
        firedb.collection(poolRecordsPath).document(docId)
                .set(poolRecordNew)
                .addOnSuccessListener(aVoid -> {
                    // Success
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(mcontext, "Error adding document", Toast.LENGTH_SHORT).show();
                });
    }
}
