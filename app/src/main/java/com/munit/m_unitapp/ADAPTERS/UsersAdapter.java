package com.munit.m_unitapp.ADAPTERS;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.munit.m_unitapp.DB.firebase;
import com.munit.m_unitapp.MODELS.User;
import com.munit.m_unitapp.R;
import com.munit.m_unitapp.ViewUserActivity;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.myViewHolder> {
    private LayoutInflater inflator;
    private List<User> data = Collections.emptyList();
    private Context mContext;
    private String SchoolId;

    firebase db = new firebase();
    String UserType;

    public UsersAdapter(Context context, List<User> data, String UserType) {
        inflator = LayoutInflater.from(context);
        mContext = context;
        this.data = data;
        this.UserType = UserType;
        this.SchoolId = SchoolId;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.layout_user, parent, false);
        myViewHolder holder = new myViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        User current = data.get(position);

        holder.name.setText( current.getName());
        holder.Role.setText( current.getUsername());
        holder.Phone.setText( current.getPhoneNo());
        if(current.getImgUrl()!=null){
            Picasso.get().load(current.getImgUrl()).memoryPolicy(MemoryPolicy.NO_STORE).centerCrop().fit().into(holder.profilePic);
//            Picasso.with(getActivity()).load(backdropURL).memoryPolicy(MemoryPolicy.NO_STORE).centerCrop().fit().into(backdropView);
        }

       //Set Image

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView Role;
        TextView Phone;
        CircularImageView profilePic;
        RelativeLayout UserView;

        public myViewHolder(final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            Role = itemView.findViewById(R.id.Role);
            Phone = itemView.findViewById(R.id.Phone);
            profilePic = itemView.findViewById(R.id.profilePic);

            UserView = itemView.findViewById(R.id.UserView);

            UserView.setOnClickListener((view) ->{
                int pos = getAdapterPosition();
                if(pos != RecyclerView.NO_POSITION) {
                    User user = data.get(pos);
                    Gson gson = new Gson();
                    String UserJson = gson.toJson(user);
                    if(UserType.equals("Admin")){ //Only Admin Can Edit
                        Intent viewR = new Intent(mContext, ViewUserActivity.class);
                        viewR.putExtra("userJson", UserJson);
                        viewR.putExtra("SchoolId", SchoolId);
                        mContext.startActivity(viewR);
                    }
                }
            });
        }
    }


}
