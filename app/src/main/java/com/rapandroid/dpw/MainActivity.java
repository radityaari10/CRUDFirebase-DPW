package com.rapandroid.dpw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private EditText etEmailLogin, etPasswordLogin;
    private TextView tvRegis;
    private Button btnLogin;

    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);

        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        }

        setContentView(R.layout.activity_main);

        etEmailLogin = findViewById(R.id.et_email_login);
        etPasswordLogin = findViewById(R.id.et_password_login);

        tvRegis = findViewById(R.id.tv_registration);
        btnLogin = findViewById(R.id.btn_login);

        tvRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regisIntent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(regisIntent);
            }
        });

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = etEmailLogin.getText().toString().trim();
                String mPass = etPasswordLogin.getText().toString().trim();

                if (TextUtils.isEmpty(mEmail)){
                    etEmailLogin.setError("Email harus diisi!!!");
                    return;
                }

                if (TextUtils.isEmpty(mPass)){
                    etPasswordLogin.setError("Password harus diisi!!!");
                    return;
                }

                mDialog.setMessage("Processing...");
                mDialog.show();

                mAuth.signInWithEmailAndPassword(mEmail, mPass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);

                            startActivity(intent);

                            Toast.makeText(getApplicationContext(), "Successful..", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(), "Failed...", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }
                    }
                });
            }
        });


    }

}
