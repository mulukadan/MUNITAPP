package com.munit.m_unitapp.UI.PAYROLL;

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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.munit.m_unitapp.ADAPTERS.EmployeesAdapter;
import com.munit.m_unitapp.DB.firebase;
import com.munit.m_unitapp.MODELS.Employee;
import com.munit.m_unitapp.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class EmployeesListActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    firebase db = new firebase();
    private FirebaseAuth mAuth;
    private Employee employee = new Employee();
    private FloatingActionMenu floatingActionMenu;
    private FloatingActionButton addUserBtn;
    private ImageView back_arrow;
    private Dialog EmployeeDialog;
    private ImageView CloseBillDialog;
    private Button SaveBtn;
    private TextView name, dateOfEmployment, dobTV;
    private RadioButton maleRB, femaleRB;
    private EditText PhoneNo;
    private EditText eSalary, jobDesc;
    private String AdminEmail, AdminPassword;
    private Spinner departmentSpiner;

    FirebaseUser Fbuser;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference ProfilePicRef;

    private EditText userName;
    private TextView email;
    private CircularImageView ProfilePic;
    private CircularImageView ChangeProfilePic;
    private Button save;
    private String ProfilePicURL;

    private Bitmap profilePicBitmap = null;

    private Dialog SelectImgDialog;
    private ImageView CloseSelectImgDialog;
    private CardView cameraCard;
    private CardView galleryCard;

    final int DOB_DATE = 0;
    final int DOE_DATE = 1;
    private int dateFor = 0;

    private Calendar calendar;
    private int year, month, day;
    private String todate;

    SweetAlertDialog sdialog;
    Bundle extras;
    int getImageOption = 0; //From 0 Camera, 1 From Gallery

    RecyclerView wokersRV;
    List<Employee> employees = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wokers_list);

        getSupportActionBar().hide();


        Fbuser = FirebaseAuth.getInstance().getCurrentUser();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        sdialog = new SweetAlertDialog(EmployeesListActivity.this, SweetAlertDialog.WARNING_TYPE);
        sdialog.setCancelable(false);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        todate = day + "/" + month + "/" + year;

        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener((view) -> {
            finish();
        });

        floatingActionMenu = findViewById(R.id.fab);
        floatingActionMenu.setClosedOnTouchOutside(true);

        addUserBtn = findViewById(R.id.addUserBtn);

        wokersRV = findViewById(R.id.wokersRV);
        wokersRV.setLayoutManager(new LinearLayoutManager(this));
        wokersRV.smoothScrollToPosition(0);

        EmployeeDialog = new Dialog(this);
        EmployeeDialog.setCanceledOnTouchOutside(false);
        EmployeeDialog.setContentView(R.layout.employee_dialog);
        EmployeeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        CloseBillDialog = EmployeeDialog.findViewById(R.id.CloseBillDialog);
        ChangeProfilePic = EmployeeDialog.findViewById(R.id.ChangeProfilePic);
        ProfilePic = EmployeeDialog.findViewById(R.id.ProfilePic);
        name = EmployeeDialog.findViewById(R.id.name);
        PhoneNo = EmployeeDialog.findViewById(R.id.PhoneNo);
        dateOfEmployment = EmployeeDialog.findViewById(R.id.dateOfEmployment);
        maleRB = EmployeeDialog.findViewById(R.id.maleRB);
        femaleRB = EmployeeDialog.findViewById(R.id.femaleRB);
        dobTV = EmployeeDialog.findViewById(R.id.dobTV);
        departmentSpiner = EmployeeDialog.findViewById(R.id.departmentSpiner);
        jobDesc = EmployeeDialog.findViewById(R.id.jobDesc);
        eSalary = EmployeeDialog.findViewById(R.id.eSalary);
        SaveBtn = EmployeeDialog.findViewById(R.id.SaveBtn);
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
        CloseBillDialog.setOnClickListener((view) -> {
            EmployeeDialog.dismiss();
        });
//        Picasso.get().load(Fbuser.getPhotoUrl()).into(ProfilePic);

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
            String gender = "Male";
            if (!maleRB.isChecked()) {
                gender = "Female";
            }
            int error = 0;
            if (uname.trim().length() < 2) {
                name.setError("Enter name");
                error = 1;
            }

            if (uPhoneNo.trim().length() != 10) {
                PhoneNo.setError("Enter Valid Number");
                error = 1;
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
                employee.setName(uname);
                employee.setPhoneNo(uPhoneNo);
                employee.setId(employees.size() + uname.substring(0, 2));
                employee.setActive(true);
                employee.setGender(gender);
                employee.setDob(dob);
                employee.setDepartment(department);
                employee.setEmploymentDate(employmentDate);
                employee.setSalary(salary);
                employee.setJobDescription(JobDescription);


                sdialog.setTitleText("Save Employee?")
                        .setContentText("Are you sure you want to add employee!")
                        .setConfirmText("Save")
                        .showCancelButton(true)
                        .setCancelClickListener(sweetAlertDialog -> {
                            sdialog.dismiss();
                        })
                        .setCancelText("Cancel")
                        .setConfirmClickListener(sDialog -> {


                            if (profilePicBitmap == null) {
                                //No Profile Image
                                employees.add(employee);
                                addWorkerToFirebase();
                            } else {
                                SaveWithImage();
                            }

                        })
                        .show();
            }
        });

        addUserBtn.setOnClickListener((view) -> {
            floatingActionMenu.close(true);
            employee = new Employee();
            showEmployeeDialog();
        });
        fetchData();
    }

    private void showEmployeeDialog() {
        if (!employee.getImgUrl().equalsIgnoreCase("")) {
            Picasso.get().load(employee.getImgUrl()).into(ProfilePic);
        }

        name.setText(employee.getName());
        PhoneNo.setText(employee.getPhoneNo());
        dateOfEmployment.setText(employee.getEmploymentDate());
//        departmentSpiner = EmployeeDialog.findViewById(R.id.departmentSpiner);
        jobDesc.setText(employee.getJobDescription());
        eSalary.setText("" + employee.getSalary());


        EmployeeDialog.show();
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

    public void fetchData() {
        SweetAlertDialog pDialog = new SweetAlertDialog(EmployeesListActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(true);
        pDialog.show();

        // Read from the database
        myRef = database.getReference("employees");
        myRef.keepSynced(true);
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
                EmployeesAdapter adapter = new EmployeesAdapter(EmployeesListActivity.this, employees);
                wokersRV.setAdapter(adapter);
                pDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                pDialog.dismiss();
                // 3. Error message
                new SweetAlertDialog(EmployeesListActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Something went wrong!")
                        .show();
            }
        });

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
                        case DOB_DATE:
                            dobTV.setText(DateDisplaying);
                            break;
                        case DOE_DATE:
                            dateOfEmployment.setText(DateDisplaying);
                            break;
                    }
                }
            };


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

    private void SaveWithImage() {
        SweetAlertDialog pDialog = new SweetAlertDialog(EmployeesListActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
            // ...

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
//    private void SaveWithImage() {
//        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
//                .setTitleText("Save Employee?")
//                .setContentText("Are you Sure you want to save these Details?")
//                .setConfirmText("Save")
//                .showCancelButton(true)
//                .setCancelText("Cancel")
//                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sDialog) {
//                        SweetAlertDialog pDialog = new SweetAlertDialog(EmployeesListActivity.this, SweetAlertDialog.PROGRESS_TYPE);
//                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#48A5F1"));
//                        pDialog.setTitleText("Updating...");
//                        pDialog.setCancelable(false);
//                        pDialog.show();
//
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        profilePicBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                        byte[] data = baos.toByteArray();
//
//                        UploadTask uploadTask = ProfilePicRef.putBytes(data);
//                        uploadTask.addOnFailureListener(exception -> {
//                            // Handle unsuccessful uploads
//                            pDialog.dismiss();
//                        }).addOnSuccessListener(taskSnapshot -> {
//                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
//                            // ...
//
//                            ProfilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                                Uri selectedImage = uri;
//                                ProfilePicURL = selectedImage.toString();
//                                Picasso.get().load(selectedImage).into(ProfilePic);
//
//
//                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                                        .setDisplayName(userName.getText().toString())
//                                        .setPhotoUri(Uri.parse(ProfilePicURL))
//                                        .build();
//
//                                Fbuser.updateProfile(profileUpdates)
//                                        .addOnCompleteListener(task -> {
//                                            if (task.isSuccessful()) {
//                                                scUser.setImgUrl(ProfilePicURL);
//                                                scUser.setName(userName.getText().toString());
//                                                updateFirebaseDb();
//                                                pDialog.dismiss();
//                                                sDialog
//                                                        .setTitleText("SUCCESS!")
//                                                        .setContentText("User profile updated Successfully!")
//                                                        .showCancelButton(false)
//                                                        .setConfirmText("OK")
//                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                                            @Override
//                                                            public void onClick(SweetAlertDialog sDialog1) {
//
//                                                                sDialog1.dismissWithAnimation();
//                                                                finish();
//                                                            }
//                                                        })
//                                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
//
//                                            }
//                                        });
//                            });
//                        });
//                    }
//                })
//                .show();
//
//    }
}