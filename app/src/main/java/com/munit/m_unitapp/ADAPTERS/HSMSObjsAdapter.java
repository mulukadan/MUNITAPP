package com.munit.m_unitapp.ADAPTERS;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.munit.m_unitapp.DB.firebase;
import com.munit.m_unitapp.HSMSObjViewActivity;
import com.munit.m_unitapp.MODELS.HSMSObject;
import com.munit.m_unitapp.R;

import java.util.Collections;
import java.util.List;

public class HSMSObjsAdapter extends RecyclerView.Adapter<HSMSObjsAdapter.myViewHolder> {
    private LayoutInflater inflator;
    private List<HSMSObject> data = Collections.emptyList();
    private Context mContext;
    private String SchoolId;

    firebase db = new firebase();
    String UserType;

    public HSMSObjsAdapter(Context context, List<HSMSObject> data) {
        inflator = LayoutInflater.from(context);
        mContext = context;
        this.data = data;
        this.UserType = UserType;
        this.SchoolId = SchoolId;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.layout_hsmsobj, parent, false);
        myViewHolder holder = new myViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        HSMSObject current = data.get(position);

        holder.name.setText(current.getSchoolName());
        holder.installDate.setText("Install Date: " + current.getInstallDate());
        holder.lastActivationDate.setText("Last Activation Date: " + current.getLastActivationDate());
        holder.ExpiryDate.setText("Expiry Date: " + current.getExpiryDate());
//        holder.ExpiryDate.setText( current.getExpiryDate());
//        if(current.getImgUrl()!=null){
//            Picasso.get().load(current.getImgUrl()).memoryPolicy(MemoryPolicy.NO_STORE).centerCrop().fit().into(holder.profilePic);
////            Picasso.with(getActivity()).load(backdropURL).memoryPolicy(MemoryPolicy.NO_STORE).centerCrop().fit().into(backdropView);
//        }

        //Set Image

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView installDate;
        TextView lastActivationDate;
        TextView ExpiryDate;
        CircularImageView profilePic;
        RelativeLayout UserView;

        public myViewHolder(final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            installDate = itemView.findViewById(R.id.installDate);
            lastActivationDate = itemView.findViewById(R.id.lastActivationDate);
            ExpiryDate = itemView.findViewById(R.id.ExpiryDate);
            profilePic = itemView.findViewById(R.id.profilePic);

            UserView = itemView.findViewById(R.id.UserView);

            UserView.setOnClickListener((view) -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    HSMSObject hsmsObject = data.get(pos);
                    Gson gson = new Gson();
                    String dataArrayJson = gson.toJson(data);
//                    if(UserType.equals("Admin")){ //Only Admin Can Edit
//                        Intent viewR = new Intent(mContext, ViewUserActivity.class);
//                        viewR.putExtra("hsmsObjectJson", hsmsObjectJson);
//                        mContext.startActivity(viewR);
//                    }
                    Intent intent = new Intent(mContext, HSMSObjViewActivity.class);
                    intent.putExtra("dataArrayJson", dataArrayJson);
                    intent.putExtra("pos", ""+pos);
                    mContext.startActivity(intent);

                }
            });
        }
    }


}
