package com.munit.m_unitapp.ADAPTERS;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.munit.m_unitapp.DB.firebase;
import com.munit.m_unitapp.MODELS.DailySales;
import com.munit.m_unitapp.MODELS.SalesCategory;
import com.munit.m_unitapp.R;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

public class SalesCategoryAdapter extends RecyclerView.Adapter<SalesCategoryAdapter.myViewHolder> {
    private LayoutInflater inflator;
    private List<SalesCategory> data = Collections.emptyList();
    private Context mContext;

    private Dialog newSalesDialog;
    private ImageView CloseDialog;
    private TextView TopupTitle;
    private TextView initialAmt;
    private EditText amt;
    private Button SaveBtn;
    String DateDisplaying;
    String AmtFor = "";

    private DailySales dailySales;

    String USERID ="2";

//    FirebaseService db = new FirebaseService();

    public SalesCategoryAdapter(Context context, List<SalesCategory> data, String DateDisplaying) {
        inflator = LayoutInflater.from(context);
        mContext = context;
        this.data = data;
        this.DateDisplaying = DateDisplaying;

    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.layout_categories, parent, false);
        getDailySale(DateDisplaying, USERID);
        newSalesDialog = new Dialog(mContext);
        newSalesDialog.setContentView(R.layout.new_sale_dialog);
        newSalesDialog.setCanceledOnTouchOutside(false);
        newSalesDialog.setCancelable(false);
        newSalesDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        amt = newSalesDialog.findViewById(R.id.amt);
        TopupTitle = newSalesDialog.findViewById(R.id.TopupTitle);
        initialAmt = newSalesDialog.findViewById(R.id.initialAmt);
        CloseDialog = newSalesDialog.findViewById(R.id.CloseDialog);
        CloseDialog.setOnClickListener(v -> {
            newSalesDialog.dismiss();
        });
        SaveBtn = newSalesDialog.findViewById(R.id.SaveBtn);

        SaveBtn.setOnClickListener(v -> {
            int amount = Integer.parseInt(amt.getText().toString());
//            new firebase().addDailySale(DateDisplaying, USERID, AmtFor, amount);
            newSalesDialog.dismiss();
        });

        myViewHolder holder = new myViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        SalesCategory current = data.get(position);
        int id = current.getId();
        String name = current.getName();
        holder.CategoryName.setText(name);

        String jsonInString = new Gson().toJson(dailySales);
        try {
            JSONObject mJSONObject = new JSONObject(jsonInString);

            String amt = mJSONObject.getString(name);
            holder.Amt.setText("Ksh. " + amt);
        } catch (JSONException e) {
            e.printStackTrace();
        }


//        getDailySale(holder,DateDisplaying, USERID, name );

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        TextView Id;
        TextView CategoryName;
        TextView Amt;
        Button editAmt;

        public myViewHolder(final View itemView) {
            super(itemView);
            editAmt = itemView.findViewById(R.id.editAmt);
            Amt = itemView.findViewById(R.id.Amt);
            CategoryName = itemView.findViewById(R.id.CategoryName);

            editAmt.setOnClickListener((view) -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    SalesCategory salesCategory = data.get(pos);
                    TopupTitle.setText(salesCategory.getName());
                    AmtFor = salesCategory.getName();
                    initialAmt.setText( Amt.getText().toString());
                    amt.setText("");
                    newSalesDialog.show();
//
//                    Gson gson = new Gson();
//                    String RoomJson = gson.toJson(room);
//
//                    Intent addI = new Intent(mContext, TenantActivity.class);
//                    addI.putExtra("RoomJson", RoomJson);
//                    mContext.startActivity(addI);
                }

            });
        }
    }
    public void getDailySale(myViewHolder holder, String date, String userId, String forWhat) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;
        String Year = date.substring(date.lastIndexOf("/") + 1);
        String Month = date.substring(date.indexOf("/") + 1, date.lastIndexOf("/"));
        String day = date.substring(0, date.indexOf("/"));

        if (Month.length() == 1) {
            Month = "0" + Month;
        }
        if (day.length() == 1) {
            day = "0" + day;
        }

        final int[] Amount = {10};
        String newDateFormt = day + "-" + Month + "-" + Year;
        String path = "depts/sales/dailysales/" + Year + "/" + Month + "/" + newDateFormt + "/" + userId;
        myRef = database.getReference(path);
//        myRef = database.getReference("msg");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String valued = dataSnapshot.getValue(String.class);
                if (valued == null){
                    valued = "0";
                }
                holder.Amt.setText("Ksh. " + valued);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                int k = 2;
            }
        });

    }

    public void getDailySale(String date, String userId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;
        String Year = date.substring(date.lastIndexOf("/") + 1);
        String Month = date.substring(date.indexOf("/") + 1, date.lastIndexOf("/"));
        String day = date.substring(0, date.indexOf("/"));

        if (Month.length() == 1) {
            Month = "0" + Month;
        }
        if (day.length() == 1) {
            day = "0" + day;
        }

        final int[] Amount = {10};
        String newDateFormt = day + "-" + Month + "-" + Year;
        String path = "depts/sales/dailysales/" + Year + "/" + Month + "/" + newDateFormt + "/" + userId;
        myRef = database.getReference(path);
//        myRef = database.getReference("msg");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                dailySales = dataSnapshot.getValue(DailySales.class);



            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                int k = 2;
            }
        });

    }

}
