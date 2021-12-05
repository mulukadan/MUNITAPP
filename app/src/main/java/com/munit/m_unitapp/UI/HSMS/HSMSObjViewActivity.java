package com.munit.m_unitapp.UI.HSMS;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.munit.m_unitapp.MODELS.HSMSObject;
import com.munit.m_unitapp.R;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.munit.m_unitapp.TOOLS.GeneralMethods.decrypt;
import static com.munit.m_unitapp.TOOLS.GeneralMethods.encrypt;

public class HSMSObjViewActivity extends AppCompatActivity {
    private ImageView back_arrow;
    Bundle extras;
    HSMSObject hsmsObject;
    TextView CLientName;
    TextView installDate;
    TextView lastActivationDate;
    TextView ExpiryDate;
    TextView CurrentKey;
    TextView cost;

    Button GenerateKeyBtn;
    Button ShareBtn;

    private Dialog GenerateKeyDialog;
    private ImageView CloseBillDialog;
    private Button genBtn;
    private Button months3;
    private Button months6;
    private Button months12;
    private EditText days;
    private TextView validTill;
    SweetAlertDialog sdialog;
    String todate, eprDate;
    LocalDate today;

    List<HSMSObject> HSMSObjs = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hsmsobj_view);
        getSupportActionBar().hide();

        //current date
        today = LocalDate.now();
        todate = String.valueOf(today);

        sdialog = new SweetAlertDialog(HSMSObjViewActivity.this, SweetAlertDialog.WARNING_TYPE);
        sdialog.setCancelable(false);

        Gson gson = new Gson();
        extras = getIntent().getExtras();
        if (extras != null) {
            int pos = Integer.parseInt(getIntent().getStringExtra("pos"));
            String dataArrayJson = getIntent().getStringExtra("dataArrayJson");
            Type listType = new TypeToken<ArrayList<HSMSObject>>() {
            }.getType();
            HSMSObjs = new Gson().fromJson(dataArrayJson, listType);

            hsmsObject = HSMSObjs.get(pos);

        } else {

        }

        CLientName = findViewById(R.id.CLientName);
        installDate = findViewById(R.id.installDate);
        lastActivationDate = findViewById(R.id.lastActivationDate);
        ExpiryDate = findViewById(R.id.ExpiryDate);
        CurrentKey = findViewById(R.id.CurrentKey);
        cost = findViewById(R.id.cost);
        GenerateKeyBtn = findViewById(R.id.GenerateKeyBtn);
        ShareBtn = findViewById(R.id.ShareBtn);

        setDataFields();

        GenerateKeyDialog = new Dialog(this);
        GenerateKeyDialog.setCanceledOnTouchOutside(false);
        GenerateKeyDialog.setContentView(R.layout.generate_key_dialog);
        GenerateKeyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        CloseBillDialog = GenerateKeyDialog.findViewById(R.id.CloseBillDialog);
        months3 = GenerateKeyDialog.findViewById(R.id.months3);
        months6 = GenerateKeyDialog.findViewById(R.id.months6);
        months12 = GenerateKeyDialog.findViewById(R.id.months12);
        genBtn = GenerateKeyDialog.findViewById(R.id.genBtn);
        days = GenerateKeyDialog.findViewById(R.id.days);
        validTill = GenerateKeyDialog.findViewById(R.id.validTill);

        validTill.setText("");

        CloseBillDialog.setOnClickListener((view) -> {
            GenerateKeyDialog.dismiss();
        });

        months3.setOnClickListener((view) -> {
            days.setText("90");
            days.setSelection(days.getText().length());
            validTill.setText("Valid till: " + nextDate(90));
        });

        months6.setOnClickListener((view) -> {
            days.setText("180");
            days.setSelection(days.getText().length());
            validTill.setText("Valid till: " + nextDate(180));
        });

        months12.setOnClickListener((view) -> {
            days.setText("365");
            days.setSelection(days.getText().length());
            validTill.setText("Valid till: " + nextDate(365));
        });

        genBtn.setOnClickListener((view) -> {
            String typed = days.getText().toString().trim();
            if (typed.length() > 0) {
                int dys = Integer.parseInt(typed);
                GenerateKey();
            } else {
                days.setError("Enter days");
            }
        });
        days.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                String typed = days.getText().toString().trim();
                if (typed.length() > 0) {
                    int dys = Integer.parseInt(typed);
                    validTill.setText("Valid till: " + nextDate(dys));
                } else {
                    validTill.setText("");
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener((view) -> {
            finish();
        });

        GenerateKeyBtn.setOnClickListener(v -> {
            GenerateKeyDialog.show();
        });

        ShareBtn.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "The Activation Key: " + hsmsObject.getActiveKey());
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String nextDate(int daysToAdd) {
        //adding one day to the localdate
        LocalDate tomorrow = today.plusDays(daysToAdd);
        String tomorrowStr = String.valueOf(tomorrow);
        eprDate = tomorrowStr;
        return tomorrowStr;
    }

    public void GenerateKey() {
        String Key = hsmsObject.getInitials() + ":" + todate + ":" + eprDate;
        String encryptedKey = encrypt(Key, 10);
        String DecryptedKey = decrypt(encryptedKey, 10);

        hsmsObject.setActiveKey(encryptedKey);
        hsmsObject.setExpiryDate(eprDate);
        hsmsObject.setLastActivationDate(todate);
        setDataFields();
        GenerateKeyDialog.dismiss();
        updateFirebase();

    }

    public void setDataFields() {
        CLientName.setText(hsmsObject.getSchoolName());
        installDate.setText(hsmsObject.getInstallDate());
        lastActivationDate.setText(hsmsObject.getLastActivationDate());
        ExpiryDate.setText(hsmsObject.getExpiryDate());
        CurrentKey.setText(hsmsObject.getActiveKey());
        cost.setText("Ksh. " + hsmsObject.getCost());
    }

    public void updateFirebase() {
        sdialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        sdialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sdialog.setTitleText("Creating Account ...")
                .setContentText("Saving new User Details!");

        sdialog.showCancelButton(false);
//        HSMSObjs.add(hsmsObject);
        myRef = database.getReference("depts/hsms/clients");
        myRef.setValue(HSMSObjs);
        sdialog.dismiss();
    }


}
