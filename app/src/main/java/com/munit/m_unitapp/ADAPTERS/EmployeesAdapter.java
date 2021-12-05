package com.munit.m_unitapp.ADAPTERS;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.munit.m_unitapp.UI.PAYROLL.EmployeeManActivity;
import com.munit.m_unitapp.MODELS.Employee;
import com.munit.m_unitapp.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class EmployeesAdapter extends RecyclerView.Adapter<EmployeesAdapter.myViewHolder> {
    private LayoutInflater inflator;
    private List<Employee> data = Collections.emptyList();
    private Context mContext;


    public EmployeesAdapter(Context context, List<Employee> data) {
        inflator = LayoutInflater.from(context);
        mContext = context;
        this.data = data;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.layout_employee, parent, false);
        myViewHolder holder = new myViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        Employee current = data.get(position);

        holder.name.setText(current.getName());
        holder.eDepartment.setText(current.getDepartment());
        holder.Phone.setText(current.getPhoneNo());
        if (!current.getImgUrl().equalsIgnoreCase("")) {
            Picasso.get().load(current.getImgUrl()).memoryPolicy(MemoryPolicy.NO_STORE).centerCrop().fit().into(holder.profilePic);
        }
        if (current.getActive()) {
            holder.statusIdcator.setBackgroundColor(Color.parseColor("#03A50A"));
        }else {
            holder.statusIdcator.setBackgroundColor(Color.parseColor("#F80404"));
        }
        //Set Image

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView eDepartment;
        TextView Phone;
        View statusIdcator;
        CircularImageView profilePic;
        RelativeLayout UserView;

        public myViewHolder(final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            eDepartment = itemView.findViewById(R.id.eDepartment);
            Phone = itemView.findViewById(R.id.Phone);
            profilePic = itemView.findViewById(R.id.profilePic);
            statusIdcator = itemView.findViewById(R.id.statusIdcator);
            UserView = itemView.findViewById(R.id.UserView);
            UserView.setOnClickListener((view) -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Employee employee = data.get(pos);
                    Gson gson = new Gson();
                    String employeeJson = gson.toJson(employee);
                    Intent viewR = new Intent(mContext, EmployeeManActivity.class);
                    viewR.putExtra("employeeJson", employeeJson);
                    mContext.startActivity(viewR);
                }
            });
        }
    }
}
