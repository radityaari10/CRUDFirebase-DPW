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

public class RegistrationActivity extends AppCompatActivity {
    private EditText etEmailRegis, etPasswordRegis;
    private TextView tvLogin;
    private Button btnRegis;

    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);

        etEmailRegis = findViewById(R.id.et_email_regis);
        etPasswordRegis = findViewById(R.id.et_password_regis);

        tvLogin = findViewById(R.id.tv_login);
        btnRegis = findViewById(R.id.btn_regis);

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = etEmailRegis.getText().toString().trim();
                String mPass = etPasswordRegis.getText().toString().trim();

                if (TextUtils.isEmpty(mEmail)){
                    etEmailRegis.setError("Email harus diisi!!!");
                    return;
                }

                if (TextUtils.isEmpty(mPass)){
                    etPasswordRegis.setError("Password harus diisi!!!");
                    return;
                }

                mDialog.setMessage("Processing....");
                mDialog.show();

                mAuth.createUserWithEmailAndPassword(mEmail, mPass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);

                            Toast.makeText(getApplicationContext(), "Successful..", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }else {
                            Toast.makeText(getApplicationContext(), "Failed...", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDialog.dismiss();
    }
}
