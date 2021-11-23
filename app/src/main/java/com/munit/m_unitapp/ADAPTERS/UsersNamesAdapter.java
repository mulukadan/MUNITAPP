package com.munit.m_unitapp.ADAPTERS;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

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

public class UsersNamesAdapter extends RecyclerView.Adapter<UsersNamesAdapter.myViewHolder> {
    private LayoutInflater inflator;
    private List<User> data = Collections.emptyList();
    private Context mContext;
    private SelectUserListener listener;
    private User selectedUser;

    public UsersNamesAdapter(Context context, List<User> data, User selectedUser) {
        inflator = LayoutInflater.from(context);
        mContext = context;
        this.data = data;
        this.selectedUser = selectedUser;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.layout_usernames, parent, false);
        myViewHolder holder = new myViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        User current = data.get(position);
        holder.name.setText(current.getName());
        if (current.getImgUrl() != null) {
            Picasso.get().load(current.getImgUrl()).memoryPolicy(MemoryPolicy.NO_STORE).centerCrop().fit().into(holder.profilePic);
        } else {
            Picasso.get().load((Uri) null).memoryPolicy(MemoryPolicy.NO_STORE).centerCrop().fit().into(holder.profilePic);
        }
        if (current.getId() == selectedUser.getId()) {
            holder.UserView.setBackgroundColor(mContext.getResources().getColor(R.color.main_green_color));
        }else{
            holder.UserView.setBackgroundColor(mContext.getResources().getColor(R.color.gray_btn_bg_color));
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        CircularImageView profilePic;
        RelativeLayout UserView;

        public myViewHolder(final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            profilePic = itemView.findViewById(R.id.profilePic);

            UserView = itemView.findViewById(R.id.UserView);

            UserView.setOnClickListener((view) -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    User user = data.get(pos);
                    listener.getSelectedUser(user);

                }
            });
        }
    }

    public void setListener(SelectUserListener listener) {
        this.listener = listener;
    }

    public interface SelectUserListener {
        void getSelectedUser(User user);
    }

}
