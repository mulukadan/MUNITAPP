package com.munit.m_unitapp.ADAPTERS;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.munit.m_unitapp.DB.firebase;
import com.munit.m_unitapp.MODELS.PoolTable;
import com.munit.m_unitapp.MODELS.User;
import com.munit.m_unitapp.R;
import com.munit.m_unitapp.UI.POOL.PoolTableActivity;
import com.munit.m_unitapp.UI.SYSUSERS.ViewUserActivity;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class PoolsAdapter extends RecyclerView.Adapter<PoolsAdapter.myViewHolder> {
    private LayoutInflater inflator;
    private List<PoolTable> data = Collections.emptyList();
    private Context mContext;
    private String SchoolId;

    firebase db = new firebase();
    String UserType;

    public PoolsAdapter(Context context, List<PoolTable> data) {
        inflator = LayoutInflater.from(context);
        mContext = context;
        this.data = data;
        this.UserType = UserType;
        this.SchoolId = SchoolId;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.layout_pool_table, parent, false);
        myViewHolder holder = new myViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        PoolTable current = data.get(position);

        holder.name.setText(current.getName());
        holder.location.setText(current.getLocation());
        holder.id.setText(current.getId()+ "");
        if (current.getColor().equalsIgnoreCase("blue")) {
            holder.imageIV.setBackgroundResource(R.drawable.blue_pool_table);
        }else if(current.getColor().equalsIgnoreCase("red")){
            holder.imageIV.setBackgroundResource(R.drawable.red_pool_table);
        }else{
            holder.imageIV.setBackgroundResource(R.drawable.green_pool_table);
        }

        //Set Image

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView location;
        TextView id;
        CircularImageView imageIV;
        RelativeLayout UserView;

        public myViewHolder(final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            location = itemView.findViewById(R.id.location);
            id = itemView.findViewById(R.id.id);
            imageIV = itemView.findViewById(R.id.imageIV);

            UserView = itemView.findViewById(R.id.UserView);

            UserView.setOnClickListener((view) -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    PoolTable poolTable = data.get(pos);
                    Gson gson = new Gson();
                    String poolTableJson = gson.toJson(poolTable);
//
                    Intent viewR = new Intent(mContext, PoolTableActivity.class);
                    viewR.putExtra("poolTableJson", poolTableJson);
                    mContext.startActivity(viewR);
                }
            });
        }
    }


}
