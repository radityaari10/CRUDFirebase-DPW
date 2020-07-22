package com.rapandroid.dpw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rapandroid.dpw.model.HomeModel;

import java.text.DateFormat;
import java.util.Date;

public class FormActivity extends AppCompatActivity {
    private EditText etName, etPrice, etDesc;
    private Button btnSave, btnCancel;
    private TextView titleForm;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private String sId, sName, sPrice, sDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable panah = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp);
        panah.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(panah);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uId = mUser.getUid();

        mDatabase =FirebaseDatabase.getInstance().getReference().child("daftar-produk-warung").child(uId);

        sId = getIntent().getStringExtra("id");
        sName = getIntent().getStringExtra("name");
        sPrice = getIntent().getStringExtra("price");
        sDesc = getIntent().getStringExtra("desc");

        etName = findViewById(R.id.et_name);
        etPrice = findViewById(R.id.et_price);
        etDesc = findViewById(R.id.et_desc);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        titleForm = findViewById(R.id.title_form);

        etName.setText(sName);
        etPrice.setText(sPrice);
        etDesc.setText(sDesc);

        if (sId.equals("")){
            btnSave.setText("Simpan");
            btnCancel.setText("Batal");
            getSupportActionBar().setTitle("Tambah Produk");
            titleForm.setText("Add Shopping List");
        }else{
            btnSave.setText("Edit");
            btnCancel.setText("Hapus");
            getSupportActionBar().setTitle("Edit Produk");
            titleForm.setText("Edit Shopping List");
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Sname = etName.getText().toString();
                Double Dprice = Double.parseDouble(etPrice.getText().toString());
                String Sdesc = etDesc.getText().toString();
                String id = mDatabase.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());

                if (btnSave.getText().equals("Simpan")){
                    addProduct(new HomeModel(Sname, Sdesc, Dprice, date, id), id);
//                    finish();
                }else{
                    editProduct(new HomeModel(Sname,Sdesc, Dprice, date, sId), sId);
//                    finish();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnCancel.getText().equals("Batal")){
                    finish();
                }else{
                    deleteProduct(sId);
                    finish();
                }
            }
        });

    }

    private void addProduct(HomeModel homeModel, String id){
        mDatabase.child(id)
                .setValue(homeModel)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        etName.setText("");
                        etDesc.setText("");
                        etPrice.setText("");
                        Toast.makeText(FormActivity.this,"Data Berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void editProduct(HomeModel homeModel, String id){
        mDatabase.child(id)
                .setValue(homeModel)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        etName.setText("");
                        etDesc.setText("");
                        etPrice.setText("");
                        Toast.makeText(FormActivity.this,"Data Berhasil diubah", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void deleteProduct(String id){
        mDatabase.child(id).removeValue();
        Toast.makeText(FormActivity.this,"Data Berhasil dihapus", Toast.LENGTH_SHORT).show();
        finish();
    }

    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home ){
//            startActivity(new Intent(FormActivity.this, HomeActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
