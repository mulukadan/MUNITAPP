package com.munit.m_unitapp.UI.SYSUSERS;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.munit.m_unitapp.DB.firebase;
import com.munit.m_unitapp.MODELS.User;
import com.munit.m_unitapp.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class UserProfileActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    FirebaseUser Fbuser;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference ProfilePicRef;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    String dbName = "m-unit";
    private User scUser = new User();
    firebase db = new firebase();

    private ImageView back_arrow;
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

    int getImageOption = 0; //From 0 Camera, 1 From Gallery

    List<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().hide();

        Fbuser = FirebaseAuth.getInstance().getCurrentUser();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        String username = Fbuser.getEmail().substring(0, Fbuser.getEmail().indexOf("@"));
        ProfilePicRef = storageRef.child("munit" + "/users/" + username + ".jpg");

        fetchData();

        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener((view) -> {
            finish();
        });

        userName = findViewById(R.id.userName);
        email = findViewById(R.id.email);
        ProfilePic = findViewById(R.id.ProfilePic);
        ChangeProfilePic = findViewById(R.id.ChangeProfilePic);
        save = findViewById(R.id.save);

        Picasso.get().load(Fbuser.getPhotoUrl()).into(ProfilePic);

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

        CloseSelectImgDialog.setOnClickListener((view) -> {
            SelectImgDialog.dismiss();
        });

        userName.setText(Fbuser.getDisplayName());
        email.setText(Fbuser.getEmail());

        ChangeProfilePic.setOnClickListener((view) -> {
            SelectImgDialog.show();
        });

        save.setOnClickListener((view) -> {
            if (profilePicBitmap == null) {
                SaveNameOnly();
            } else {
                SaveNameAndImage();
            }
        });

    }

    private void SaveNameAndImage() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("UPDATE YOUR DETAILS?")
                .setContentText("Are you Sure you want to save these Details?")
                .setConfirmText("Save")
                .showCancelButton(true)
                .setCancelText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        SweetAlertDialog pDialog = new SweetAlertDialog(UserProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#48A5F1"));
                        pDialog.setTitleText("Updating...");
                        pDialog.setCancelable(false);
                        pDialog.show();

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        profilePicBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = ProfilePicRef.putBytes(data);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                pDialog.dismiss();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                // ...

                                ProfilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    Uri selectedImage = uri;
                                    ProfilePicURL = selectedImage.toString();
                                    Picasso.get().load(selectedImage).into(ProfilePic);


                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(userName.getText().toString())
                                            .setPhotoUri(Uri.parse(ProfilePicURL))
                                            .build();

                                    Fbuser.updateProfile(profileUpdates)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    scUser.setImgUrl(ProfilePicURL);
                                                    scUser.setName(userName.getText().toString());
                                                    updateFirebaseDb();
                                                    pDialog.dismiss();
                                                    sDialog
                                                            .setTitleText("SUCCESS!")
                                                            .setContentText("User profile updated Successfully!")
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

                                                }
                                            });
                                });
                            }
                        });
                    }
                })
                .show();

    }

    private void updateFirebaseDb() {
        for (User u : users) {
            if (u.getUsername().equals(Fbuser.getEmail())) {
                users.remove(u);
                users.add(scUser);
                db.saveUsers(users);
                break;
            }
        }
    }

    private void SaveNameOnly() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("UPDATE YOUR DETAILS?")
                .setContentText("Are you Sure you want to save these Details?")
                .setConfirmText("Save")
                .showCancelButton(true)
                .setCancelText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        SweetAlertDialog pDialog = new SweetAlertDialog(UserProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#48A5F1"));
                        pDialog.setTitleText("Updating...");
                        pDialog.setCancelable(false);
                        pDialog.show();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(userName.getText().toString())
//                                .setPhotoUri(Uri.parse(ProfilePicURL))
                                .build();

                        Fbuser.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
//                                            scUser.setImgUrl(ProfilePicURL);
                                            scUser.setName(userName.getText().toString());
                                            updateFirebaseDb();
                                            pDialog.dismiss();
                                            sDialog
                                                    .setTitleText("SUCCESS!")
                                                    .setContentText("User profile updated Successfully!")
                                                    .showCancelButton(false)
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sDialog) {
                                                            sDialog.dismissWithAnimation();
                                                            finish();
                                                        }
                                                    })
                                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                                        }
                                    }
                                });

                    }
                })
                .show();

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
            EasyPermissions.requestPermissions(this, "The App needs permission to set your profile Picture",
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

    public void fetchData() {
        SweetAlertDialog pDialog = new SweetAlertDialog(UserProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(true);
        pDialog.show();

        myRef = database.getReference( "users");
        myRef.keepSynced(true);

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String Key = postSnapshot.getKey();
                    User user = postSnapshot.getValue(User.class);
//                      if(room_ic.getId().equals("l Room 12"))
                    users.add(user);
//                    Rooms.add(room_ic);

                }

                for (User u : users) {
                    if (u.getUsername().equals(Fbuser.getEmail())) {
                        scUser = u;
                        break;
                    }
                }

                pDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
                pDialog.dismiss();

                // 3. Error message
                new SweetAlertDialog(UserProfileActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Something went wrong!")
                        .show();
            }
        });

    }
}