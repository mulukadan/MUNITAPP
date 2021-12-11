package com.munit.m_unitapp.DB;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.munit.m_unitapp.MODELS.DailySales;
import com.munit.m_unitapp.MODELS.PoolTable;
import com.munit.m_unitapp.MODELS.PoolTableRecord;
import com.munit.m_unitapp.MODELS.User;
import com.munit.m_unitapp.MODELS.Employee;

import java.util.ArrayList;
import java.util.List;

public class firebase {
    FirebaseDatabase database;
    String salesCategoriesPath;
    private DatabaseReference myRef;

    public firebase() {
        database = FirebaseDatabase.getInstance();
        salesCategoriesPath = "depts/sales/categories";
    }

    public String getSalesCategoriesPath() {
        return salesCategoriesPath;
    }

    public void setSalesCategoriesPath(String salesCategoriesPath) {
        this.salesCategoriesPath = salesCategoriesPath;
    }

    public DatabaseReference getMyRef() {
        return myRef;
    }

    public void setMyRef(DatabaseReference myRef) {
        this.myRef = myRef;
    }


    public void addDailySale(String userId, DailySales dailySales) {
        String date = dailySales.getDate();
        String Year = date.substring(date.lastIndexOf("/") + 1);
        String Month = date.substring(date.indexOf("/") + 1, date.lastIndexOf("/"));
        String day = date.substring(0, date.indexOf("/"));
//        day ="17";
        if (Month.length() == 1) {
            Month = "0" + Month;
        }
        if (day.length() == 1) {
            day = "0" + day;
        }
        String newDateFormt = Year + "-" + Month + "-" + day;
        String path = "depts/sales/dailysales/" + userId + "/" + newDateFormt;
        myRef = database.getReference(path);
        myRef.setValue(dailySales);
    }

    public void addPoolSale(PoolTableRecord record) {
        String date = record.getDate();
        String Year = date.substring(date.lastIndexOf("/") + 1);
        String Month = date.substring(date.indexOf("/") + 1, date.lastIndexOf("/"));
        String day = date.substring(0, date.indexOf("/"));
//        day ="17";
        if (Month.length() == 1) {
            Month = "0" + Month;
        }
        if (day.length() == 1) {
            day = "0" + day;
        }
        String newDateFormt = Year + "-" + Month + "-" + day;
        String path = "depts/pool/collections/" + newDateFormt;
        myRef = database.getReference(path);
        myRef.setValue(record);
    }

    public void saveUsers(List<User> users) {
        myRef = database.getReference("users");
        myRef.setValue(users);
    }
    public void savePoolTables(List<PoolTable> poolTables) {
        myRef = database.getReference("depts/pool/pooltables");
        myRef.setValue(poolTables);
    }
    public void saveEmployees(List<Employee> employees) {
        myRef = database.getReference("employees");
        myRef.setValue(employees);
    }

    public List<DailySales> getDailySalesForDays(List<String> Dates, String userId){
        int count = Dates.size();
        List<DailySales> Sales = new ArrayList<>();
        for(int i = 0; i < count; i++){
            DailySales dailySales = getDailySale(Dates.get(i), userId);
            Sales.add(dailySales);
        }
        return Sales;

    }

    public PoolTableRecord getPoolSale(String date) {
        final PoolTableRecord[] records = new PoolTableRecord[1];
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
        String newDateFormt = Year + "-" + Month + "-" + day;
        String path = "depts/pool/" + newDateFormt;
        myRef = database.getReference(path);
//        myRef = database.getReference("msg");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                records[0] = dataSnapshot.getValue(PoolTableRecord.class);

                if (records[0] == null) {
                    records[0] = new PoolTableRecord();
                    records[0].setDate(date);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                int k = 2;
            }
        });
        return records[0];
    }

    public DailySales getDailySale(String date, String userId) {
        final DailySales[] dailySales = new DailySales[1];
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
        String newDateFormt = Year + "-" + Month + "-" + day;
        String path = "depts/sales/dailysales/" + userId + "/" + newDateFormt;
        myRef = database.getReference(path);
//        myRef = database.getReference("msg");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                dailySales[0] = dataSnapshot.getValue(DailySales.class);

                if (dailySales[0] == null) {
                    dailySales[0] = new DailySales();
                    dailySales[0].setDate(date);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                int k = 2;
            }
        });
        return dailySales[0];
    }
}
