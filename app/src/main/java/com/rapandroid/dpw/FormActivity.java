package com.rapandroid.dpw;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.rapandroid.dpw.model.HomeModel;

import java.text.DateFormat;
import java.util.Date;

public class FormActivity extends AppCompatActivity {
    private EditText etName, etPrice, etDesc;
    private Button btnSave, btnCancel, btnChoose;
    private TextView titleForm;
    private ImageView ivProduct;
    private boolean statusChooser = false;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mStorageReference, ref;
    public Uri imguri;
    private StorageTask uploadTask;

    private String sId, sName, sPrice, sDesc, sFoto;

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
        mStorageReference = FirebaseStorage.getInstance().getReference("Images");
        ref = FirebaseStorage.getInstance().getReference();

        sId = getIntent().getStringExtra("id");
        sName = getIntent().getStringExtra("name");
        sPrice = getIntent().getStringExtra("price");
        sDesc = getIntent().getStringExtra("desc");
        sFoto = getIntent().getStringExtra("img");

        etName = findViewById(R.id.et_name);
        etPrice = findViewById(R.id.et_price);
        etDesc = findViewById(R.id.et_desc);
        ivProduct = findViewById(R.id.iv_product);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        btnChoose = findViewById(R.id.btn_choose);
        titleForm = findViewById(R.id.title_form);

        if (sId.equals("")){
            btnSave.setText("Simpan");
            btnCancel.setText("Batal");
            getSupportActionBar().setTitle("Tambah Produk");
            titleForm.setText("Add Shopping List");
        }else{
            etName.setText(sName);
            etPrice.setText(sPrice);
            etDesc.setText(sDesc);
            btnSave.setText("Edit");
            btnCancel.setText("Hapus");
            getSupportActionBar().setTitle("Edit Produk");
            titleForm.setText("Edit Shopping List");
            ref.child(sFoto).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getApplicationContext())
                            .load(uri)
                            .into(ivProduct);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileChooser();
                statusChooser = true;
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Sname = etName.getText().toString();
                Double Dprice = Double.parseDouble(etPrice.getText().toString());
                String Sdesc = etDesc.getText().toString();
                String id = mDatabase.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());

                if (btnSave.getText().equals("Simpan")){
                    if (statusChooser){
                        fileUploader(id);
                        addProduct(new HomeModel(Sname, Sdesc, Dprice, date, id), id);
//                        statusChooser = false;
                    }else{
                        addProduct(new HomeModel(Sname, Sdesc, Dprice, date, id), id);
//                        statusChooser = false;
                    }
                    statusChooser = false;
                }else{
                    if (statusChooser){
                        fileUploader(sId);
                        editProduct(new HomeModel(Sname,Sdesc, Dprice, date, sId), sId);
                    }else {
                        editProduct(new HomeModel(Sname,Sdesc, Dprice, date, sId), sId);
                    }
                    statusChooser = false;
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
                    fileDeleted(sFoto);
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

    private void fileChooser(){
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    private String getExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void fileUploader(String id){
        StorageReference ref =  mStorageReference.child(id + "." + getExtension(imguri));

        uploadTask = ref.putFile(imguri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void fileDeleted(String id){
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(id);

        ref.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imguri = data.getData();
            ivProduct.setImageURI(imguri);
        }
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
