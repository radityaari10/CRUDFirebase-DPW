package com.rapandroid.dpw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rapandroid.dpw.adapter.CardViewHomeAdapater;
import com.rapandroid.dpw.model.HomeModel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private ArrayList<HomeModel> list;
    private CardViewHomeAdapater cardViewHomeAdapater;

    private RecyclerView rcProduct;
    private LinearLayoutManager mManager;
    private FloatingActionButton fabAdd;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        String uId = mUser.getUid();
//
        mDatabase = FirebaseDatabase.getInstance().getReference().child("daftar-produk-warung").child(uId);

        mDatabase.keepSynced(true);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mUser = firebaseAuth.getCurrentUser();
                if (mUser == null){
                    startActivity(new Intent(HomeActivity.this, MainActivity.class));
                    finish();
                }
            }
        };

        rcProduct = findViewById(R.id.rc_product);
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        rcProduct.setHasFixedSize(true);
        rcProduct.setLayoutManager(mManager);
        rcProduct.setItemAnimator(new DefaultItemAnimator());

        progressDialog = ProgressDialog.show(HomeActivity.this,
                null,
                "Tunggu sebentar",
                true,
                false);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list =  new ArrayList<>();
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()){
                    HomeModel homeModel = noteDataSnapshot.getValue(HomeModel.class);
//                    homeModel.setId(dataSnapshot.getKey());
//                    homeModel.setId(dataSnapshot.getRef().getKey());
//                    homeModel.setId(String.valueOf(dataSnapshot.child("id").getValue()));
                    list.add(homeModel);
                }
                cardViewHomeAdapater = new CardViewHomeAdapater(getApplicationContext(), list);
                rcProduct.setAdapter(cardViewHomeAdapater);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan",Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });

        fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FormActivity.class)
                    .putExtra("name", "")
                    .putExtra("price", "")
                    .putExtra("desc", "")
                    .putExtra("id", "");
                startActivity(intent);
            }
        });
    }


    public boolean onCreateOptionsMenu(Menu mn){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, mn);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mt){
        switch (mt.getItemId()){
            case R.id.option_setting:
                Toast.makeText(getApplicationContext(), "Pengaturan dalam proses", Toast.LENGTH_SHORT).show();
                break;
            case R.id.option_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intToMain = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intToMain);
                Toast.makeText(getApplicationContext(), "Anda Berhasil Keluar..", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog.dismiss();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener!=null){
            mAuth.removeAuthStateListener(authStateListener);
        }
    }
}
