package com.munit.m_unitapp.ADAPTERS;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.munit.m_unitapp.MODELS.PoolRecordNew;
import com.munit.m_unitapp.MODELS.PoolTableRecord;
import com.munit.m_unitapp.R;

import java.util.Collections;
import java.util.List;

public class PoolRecordsAdapter_New extends RecyclerView.Adapter<PoolRecordsAdapter_New.myViewHolder> {
    private LayoutInflater inflator;
    private List<PoolRecordNew> data = Collections.emptyList();
    private Context mContext;

//    FirebaseService db = new FirebaseService();

    public PoolRecordsAdapter_New(Context context, List<PoolRecordNew> data) {
        inflator = LayoutInflater.from(context);
        mContext = context;
        this.data = data;
        Collections.reverse(data);
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.layout_record_new, parent, false);
        myViewHolder holder = new myViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {

        PoolRecordNew current = data.get(position);
        holder.Date.setText(current.getDate());
        holder.Total.setText(currencyFormatter(current.getAmount()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        TextView Date;
        TextView Total;
        LinearLayout biz2Layout;

//        ImageButton viewBtn, addBtn;

        public myViewHolder(final View itemView) {
            super(itemView);
            Date = itemView.findViewById(R.id.date);
            Total = itemView.findViewById(R.id.Total);
            biz2Layout = itemView.findViewById(R.id.biz2Layout);

//            addBtn.setOnClickListener((view) ->{
//                int pos = getAdapterPosition();
//                if(pos != RecyclerView.NO_POSITION) {
//                    PoolTableRecord room = data.get(pos);
//                    Gson gson = new Gson();
//                    String RoomJson = gson.toJson(room);
//
//                    Intent addI = new Intent(mContext, PoolRecordsAdapter.class);
//                    addI.putExtra("RoomJson", RoomJson);
//                    mContext.startActivity(addI);
//                }
//
//            });
        }
    }

    public String currencyFormatter(double amount) {
        return String.format("%,.2f", amount);
    }

}