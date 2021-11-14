package com.munit.m_unitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.munit.m_unitapp.ADAPTERS.EmployeesAdapter;
import com.munit.m_unitapp.ADAPTERS.PaymentsAdapter;
import com.munit.m_unitapp.ADAPTERS.PoolRecordsAdapter;
import com.munit.m_unitapp.DB.firebase;
import com.munit.m_unitapp.MODELS.Employee;
import com.munit.m_unitapp.MODELS.EmployeePayment;
import com.munit.m_unitapp.MODELS.User;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class EmployeeManActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    firebase db = new firebase();
    private ImageView back_arrow, editEmBtn;
    private TextView nameTV, titleTV, GenderTV, ageTV, emplyDateTV, salaryTV, emplymentStatusTV, totalAdvance, totalSalary, PaymentTitle;
    private CircularImageView ProfilePic, dProfilePic;
    private RecyclerView paymentsRV;

    private Dialog EmployeeDialog, advanceDialog;
    private ImageView CloseBillDialog, CloseAdvDialog;
    private RadioButton maleRB, femaleRB;
    private Button AdvSaveBtn, SaveBtn;
    private TextView name, dateOfEmployment, advDateTV, dobTV;
    private EditText PhoneNo,
            advAmtET, advReasonET, advDateEt;
    private EditText eSalary, jobDesc;
    private String AdminEmail, AdminPassword;
    private Spinner departmentSpiner;
    private CircularImageView ChangeProfilePic;
    private String ProfilePicURL;

    SweetAlertDialog sdialog;
    private Bitmap profilePicBitmap = null;

    private Dialog SelectImgDialog;
    private ImageView CloseSelectImgDialog;
    private CardView cameraCard;
    private CardView galleryCard;
    int getImageOption = 0; //From 0 Camera, 1 From Gallery

    private RelativeLayout advBtn, payBtn;
    private Calendar calendar;
    private int year, month, day;
    private String todate;

    Bundle extras;
    private Employee employee;
    FirebaseUser Fbuser;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference ProfilePicRef;

    List<Employee> employees = new ArrayList<>();
    List<EmployeePayment> payments = new ArrayList<>();
    PaymentsAdapter transactionsTypesAdapter;
    final int DOB_DATE = 0;
    final int DOE_DATE = 1;
    final int DOA_DATE = 2;
    private int dateFor = 0;
    FirebaseUser user;
    private String Username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_man);
        getSupportActionBar().hide();

        user = FirebaseAuth.getInstance().getCurrentUser();
        Username = user.getDisplayName();
        if (Username == null || Username.length() < 1) {
            Username = user.getEmail().substring(0, user.getEmail().indexOf("@"));
        }

        back_arrow = findViewById(R.id.back_arrow);
        editEmBtn = findViewById(R.id.editEmBtn);
        nameTV = findViewById(R.id.nameTV);
        titleTV = findViewById(R.id.titleTV);
        GenderTV = findViewById(R.id.GenderTV);
        ageTV = findViewById(R.id.ageTV);
        emplyDateTV = findViewById(R.id.emplyDateTV);
        salaryTV = findViewById(R.id.salaryTV);
        ProfilePic = findViewById(R.id.ProfilePic);
        emplymentStatusTV = findViewById(R.id.emplymentStatusTV);
        advBtn = findViewById(R.id.advBtn);
        payBtn = findViewById(R.id.payBtn);

        paymentsRV = findViewById(R.id.paymentsRV);
        paymentsRV.setLayoutManager(new LinearLayoutManager(this));
        paymentsRV.smoothScrollToPosition(0);


        advanceDialog = new Dialog(this);
        advanceDialog.setCanceledOnTouchOutside(false);
        advanceDialog.setContentView(R.layout.give_advance_dialog);
        advanceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        CloseAdvDialog = advanceDialog.findViewById(R.id.CloseAdvDialog);
        advAmtET = advanceDialog.findViewById(R.id.advAmtET);
        advReasonET = advanceDialog.findViewById(R.id.advReasonET);
        advDateTV = advanceDialog.findViewById(R.id.advDateTV);
        AdvSaveBtn = advanceDialog.findViewById(R.id.SaveBtn);
        PaymentTitle = advanceDialog.findViewById(R.id.PaymentTitle);

        totalAdvance = advanceDialog.findViewById(R.id.totalAdvance);
        totalSalary = advanceDialog.findViewById(R.id.totalSalary);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        todate = day + "/" + month + "/" + year;

        advDateTV.setText(todate);

        CloseAdvDialog.setOnClickListener(v -> {
            advanceDialog.dismiss();
        });

        advDateTV.setOnClickListener(v -> {
            dateFor = DOA_DATE;
            showDialog(999);
        });

        EmployeeDialog = new Dialog(this);
        EmployeeDialog.setCanceledOnTouchOutside(false);
        EmployeeDialog.setContentView(R.layout.employee_dialog);
        EmployeeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        CloseBillDialog = EmployeeDialog.findViewById(R.id.CloseBillDialog);
        ChangeProfilePic = EmployeeDialog.findViewById(R.id.ChangeProfilePic);
        dProfilePic = EmployeeDialog.findViewById(R.id.ProfilePic);
        name = EmployeeDialog.findViewById(R.id.name);
        PhoneNo = EmployeeDialog.findViewById(R.id.PhoneNo);
        dateOfEmployment = EmployeeDialog.findViewById(R.id.dateOfEmployment);
        dobTV = EmployeeDialog.findViewById(R.id.dobTV);
        departmentSpiner = EmployeeDialog.findViewById(R.id.departmentSpiner);
        jobDesc = EmployeeDialog.findViewById(R.id.jobDesc);
        eSalary = EmployeeDialog.findViewById(R.id.eSalary);
        SaveBtn = EmployeeDialog.findViewById(R.id.SaveBtn);
        maleRB = EmployeeDialog.findViewById(R.id.maleRB);
        femaleRB = EmployeeDialog.findViewById(R.id.femaleRB);
        dateOfEmployment.setText(todate);
        dobTV.setText(todate);

        dobTV.setOnClickListener(v -> {
            dateFor = DOB_DATE;
            showDialog(999);
        });
        dateOfEmployment.setOnClickListener(v -> {
            dateFor = DOE_DATE;
            showDialog(999);
        });

        sdialog = new SweetAlertDialog(EmployeeManActivity.this, SweetAlertDialog.WARNING_TYPE);
        sdialog.setCancelable(false);

        Gson gson = new Gson();
        extras = getIntent().getExtras();
        if (extras != null) {
            employee = gson.fromJson(getIntent().getStringExtra("employeeJson"), Employee.class);
        } else {

        }

        back_arrow.setOnClickListener((view) -> {
            finish();
        });

        editEmBtn.setOnClickListener((view) -> {
            populateEMployeeUI();
        });

        CloseBillDialog.setOnClickListener((view) -> {
            EmployeeDialog.dismiss();
        });

        advBtn.setOnClickListener((view) -> {
            if (employee.getActive()) {
                clearAdvDialog();
                PaymentTitle.setText("Salary Advance");
                totalAdvance.setText("Ksh. " + employee.getAdvance());
                totalSalary.setText("Ksh. " + employee.getSalary());
                advReasonET.setVisibility(View.VISIBLE);
                advanceDialog.show();
            } else {
                Toast.makeText(this, "Employee Status not Active", Toast.LENGTH_SHORT).show();
            }

        });

        payBtn.setOnClickListener((view) -> {
            if (employee.getActive()) {
                clearAdvDialog();
                PaymentTitle.setText("Salary Payment");
                totalAdvance.setText("Ksh. " + employee.getAdvance());
                totalSalary.setText("Ksh. " + employee.getSalary());
                advAmtET.setText("" + (employee.getSalary() - employee.getAdvance()));
                advAmtET.setSelection(advAmtET.getText().length());
                advReasonET.setText("Salary Payment");
                advReasonET.setVisibility(View.GONE);
                advanceDialog.show();
            } else {
                Toast.makeText(this, "Employee Status not Active", Toast.LENGTH_SHORT).show();
            }
        });

        AdvSaveBtn.setOnClickListener(v -> {
            int err = 0;
            String amtSt = advAmtET.getText().toString().trim();
            int amt = 0;
            String reason = advReasonET.getText().toString().trim();
            String date = advDateTV.getText().toString().trim();

            if (amtSt.length() < 1) {
                err = 1;
                advAmtET.setError("Enter Valid Amt");
                advAmtET.requestFocus();
            } else {
                try {
                    amt = Integer.parseInt(amtSt);
                } catch (Exception e) {
                    err = 1;
                    advAmtET.setError("Enter Valid Amt");
                    advAmtET.requestFocus();
                }
            }

            if (reason.length() < 5) {
                err = 1;
                advReasonET.setError("Enter Valid Reason");
                advReasonET.requestFocus();
            }

            if (err == 0) {
                int all = employee.getPayments().size();
                String id = all + "_" + date;
                int amount = amt;
                String description = reason;
                String type;
                int currentAdv;
                int initialAdvTotal = employee.getAdvance();
                String dTitle, dQuestion;
                if (PaymentTitle.getText().toString().contains("Advance")) {
                    type = "A";
                    currentAdv = initialAdvTotal + amt;
                    dTitle = "Give Advance?";
                    dQuestion = "Are you sure you want to give advance of Ksh. " + amt + " to " + employee.getName() + "?";
                } else {
                    type = "S";
                    int sattledAdvance = employee.getSalary() - amt;
                    currentAdv = initialAdvTotal - sattledAdvance;

                    dTitle = "Pay Salary?";
                    dQuestion = "Are you sure you want to PAY Ksh. " + amt + " to " + employee.getName() + "?";
                }


                sdialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                int finalcurrentAdv = currentAdv;
                int InitialAdvTotal1 = initialAdvTotal;
                sdialog.setTitleText(dTitle)
                        .setContentText(dQuestion)
                        .setConfirmText("Proceed")
                        .showCancelButton(true)
                        .setCancelClickListener(sweetAlertDialog -> {
                            sdialog.dismiss();
                        })
                        .setCancelText("Cancel")
                        .setConfirmClickListener(sDialog -> {
                            int idex = employee.getIdex(employees, employee.getId());
//                            employees.remove(idex);
                            EmployeePayment payment = new EmployeePayment(id, amount, description, InitialAdvTotal1, finalcurrentAdv, type);
                            payment.setPaidBy(Username);
                            employee.getPayments().add(payment);
                            employee.setAdvance(finalcurrentAdv);
                            employees.set(idex, employee);
//                            employees.add(employee);
                            addWorkerToFirebase();
                            advanceDialog.dismiss();

                            updateUI();
                        })
                        .show();
            }

        });

        SelectImgDialog = new Dialog(this);
        SelectImgDialog.setCanceledOnTouchOutside(false);
        SelectImgDialog.setContentView(R.layout.photo_dialog);

        CloseSelectImgDialog = SelectImgDialog.findViewById(R.id.closedialog);
        cameraCard = SelectImgDialog.findViewById(R.id.cameraCard);
        galleryCard = SelectImgDialog.findViewById(R.id.galleryCard);

        cameraCard.setOnClickListener((view) -> {
            getImageOption = 0; // Camera
            GetImage();
        });
        galleryCard.setOnClickListener((view) -> {
            getImageOption = 1; // Gallery
            GetImage();
        });

        ChangeProfilePic.setOnClickListener((view) -> {
            SelectImgDialog.show();
        });

        SaveBtn.setOnClickListener((view) -> {
            String uname = name.getText().toString();
            String uPhoneNo = PhoneNo.getText().toString().trim();
            String salaryString = eSalary.getText().toString().trim();
            int error = 0;
            if (uname.trim().length() < 2) {
                name.setError("Enter name");
                error = 1;
            }

            if (uPhoneNo.trim().length() != 10) {
                PhoneNo.setError("Enter Valid Number");
                error = 1;
            }

            String gender = "Male";
            if (!maleRB.isChecked()) {
                gender = "Female";
            }
            int salary = 0;
            try {
                salary = Integer.parseInt(salaryString);
            } catch (Exception e) {
                eSalary.setError("Enter Valid Salary");
                error = 1;
            }

            String employmentDate = dateOfEmployment.getText().toString();
            String dob = dobTV.getText().toString();
            String department = departmentSpiner.getSelectedItem().toString();
            String JobDescription = jobDesc.getText().toString().trim();

            if (error == 0) {
                EmployeeDialog.dismiss();
//                Copy Old EMployee Obj to new Updated one
                String tmp = gson.toJson(employee);
                Employee employee1 = gson.fromJson(tmp, Employee.class);

                employee1.setName(uname);
                employee1.setPhoneNo(uPhoneNo);
                employee1.setGender(gender);
//                employee1.setActive(true);
                employee1.setDepartment(department);
                employee1.setEmploymentDate(employmentDate);
                employee1.setDob(dob);
                employee1.setSalary(salary);
                employee1.setJobDescription(JobDescription);

                sdialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                sdialog.setTitleText("Update Employee?")
                        .setContentText("Are you sure you want to update employee details!")
                        .setConfirmText("Update")
                        .showCancelButton(true)
                        .setCancelClickListener(sweetAlertDialog -> {
                            sdialog.dismiss();
                        })
                        .setCancelText("Cancel")
                        .setConfirmClickListener(sDialog -> {
                            int idex = employee.getIdex(employees, employee.getId());
                            employees.remove(idex);
//                Copy Updated Employee Obj to old one
                            String tmp2 = gson.toJson(employee1);
                            employee = gson.fromJson(tmp2, Employee.class);
                            if (profilePicBitmap == null) {
                                //No Profile Image
                                employees.add(employee);
                                addWorkerToFirebase();
                            } else {
                                SaveWithImage();
                            }
                            updateUI();
                        })
                        .show();
            }
        });


        transactionsTypesAdapter = new PaymentsAdapter(this, payments);
        paymentsRV.setAdapter(transactionsTypesAdapter);
        fetchData();
        updateUI();

    }

    public void clearAdvDialog() {
        advAmtET.setText("");
        advReasonET.setText("");
        advDateTV.setText(todate);
    }

    public void updateUI() {
        if (employee.getImgUrl().length() > 2) {
            Picasso.get().load(employee.getImgUrl()).into(ProfilePic);
        }

        nameTV.setText(employee.getName());
        titleTV.setText(employee.getJobDescription() + "\n" + employee.getDepartment());
        GenderTV.setText(employee.getGender());
        ageTV.setText(employee.getEmploymentDate());
        emplyDateTV.setText(employee.getEmploymentDate());
        salaryTV.setText("Ksh. " + employee.getSalary());
        emplymentStatusTV.setText("Employment Status: " + employee.getActive().toString());

        //Update RV
        payments.clear();
        payments.addAll(employee.getPaymentsReversed());
        transactionsTypesAdapter.notifyDataSetChanged();

    }

    private void addWorkerToFirebase() {
        sdialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        sdialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sdialog.setTitleText("Saving...")
                .setContentText("Saving new Employee Details!");
        sdialog.showCancelButton(false);
        db.saveEmployees(employees);
        EmployeeDialog.dismiss();
        sdialog.dismiss();
    }

    private void SaveWithImage() {
        SweetAlertDialog pDialog = new SweetAlertDialog(EmployeeManActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#48A5F1"));
        pDialog.setTitleText("Updating...");
        pDialog.setCancelable(false);
        pDialog.show();

        ProfilePicRef = storageRef.child("munit" + "/employees/" + employee.getId() + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        profilePicBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = ProfilePicRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            pDialog.dismiss();
        }).addOnSuccessListener(taskSnapshot -> {
            ProfilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Uri selectedImage = uri;
                ProfilePicURL = selectedImage.toString();
                Picasso.get().load(selectedImage).into(ProfilePic);

                employee.setImgUrl(ProfilePicURL);
                employees.add(employee);
                addWorkerToFirebase();
                pDialog.dismiss();
                sdialog
                        .setTitleText("SUCCESS!")
                        .setContentText("Employee profile updated Successfully!")
                        .showCancelButton(false)
                        .setConfirmText("OK")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog1) {
                                sDialog1.dismissWithAnimation();
                                finish();
                            }
                        })
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
            });
        });

    }

    public void fetchData() {
        SweetAlertDialog pDialog = new SweetAlertDialog(EmployeeManActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(true);
        pDialog.show();

        // Read from the database
        myRef = database.getReference("employees");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                employees.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String Key = postSnapshot.getKey();
                    Employee employee = postSnapshot.getValue(Employee.class);
//                      if(room_ic.getId().equals("l Room 12"))
                    employees.add(employee);
                }
                pDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                pDialog.dismiss();
                // 3. Error message
                new SweetAlertDialog(EmployeeManActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Something went wrong!")
                        .show();
            }
        });

    }

    private void populateEMployeeUI() {
        if (employee.getImgUrl().length() > 2) {
            Picasso.get().load(employee.getImgUrl()).into(dProfilePic);
        }
        if (employee.getGender() == null || employee.getGender().equals("Male")) {
            maleRB.setChecked(true);
        } else {
            femaleRB.setChecked(true);
        }
        name.setText(employee.getName());
        PhoneNo.setText(employee.getPhoneNo());
        eSalary.setText("" + employee.getSalary());
        dateOfEmployment.setText(employee.getEmploymentDate());
        dobTV.setText(employee.getDob());
        jobDesc.setText(employee.getJobDescription());
        departmentSpiner.setSelection(((ArrayAdapter) departmentSpiner.getAdapter()).getPosition(employee.getDepartment()));
        EmployeeDialog.show();
    }

    @AfterPermissionGranted(123)
    private void GetImage() {
        SelectImgDialog.dismiss();
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            //Permission Granted, Load Gallery
            if (getImageOption == 0) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 0);
            } else {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
            }

        } else {
            //No Permission
            EasyPermissions.requestPermissions(this, "The App needs permission to set a profile Picture",
                    123, perms);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    profilePicBitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    ProfilePic.setImageBitmap(profilePicBitmap);
//                    Uri selectedImage = (Uri) imageReturnedIntent.getExtras().get("data");
//                    ProfilePicURL = selectedImage.toString();
//                    Picasso.get().load(selectedImage).into(ProfilePic);
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri selectedImage = imageReturnedIntent.getData();
                        profilePicBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        ProfilePic.setImageBitmap(profilePicBitmap);
                    } catch (Exception e) {

                    }
                }
                break;
            case AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE:
                //From Settings permission
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        } catch (Exception e) {

        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        GetImage();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    myDateListener, year, month - 1, day);
            datePickerDialog.setCanceledOnTouchOutside(false);
            return datePickerDialog;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year, arg2 = month, arg3 = day

                    String DateDisplaying = arg3 + "/" + (arg2 + 1) + "/" + arg1;
                    switch (dateFor) {
                        case DOA_DATE:
                            advDateTV.setText(DateDisplaying);
                            break;
                        case DOB_DATE:
                            dobTV.setText(DateDisplaying);
                            break;
                        case DOE_DATE:
                            dateOfEmployment.setText(DateDisplaying);
                            break;
                    }


                }
            };

}