package com.example.gargianimalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText email;
    private Button recoverbtn;
    private FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email=findViewById(R.id.forget_email);
        recoverbtn=findViewById(R.id.recover);
        mauth=FirebaseAuth.getInstance();

        recoverbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

    }

    private void validate(){
        String e=email.getText().toString();
        if(e.isEmpty()){
            email.setError("Required");
        }else{
            forgetPass(e);
        }
    }

    private void forgetPass(String e) {
       mauth.sendPasswordResetEmail(e).addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull  Task<Void> task) {
               if(task.isSuccessful()){
                   Toast.makeText(ForgotPasswordActivity.this, "Please check your mail", Toast.LENGTH_SHORT).show();
                   startActivity(new Intent(ForgotPasswordActivity.this,LoginViaEmail.class));
                   finish();
               }else{
                   Toast.makeText(ForgotPasswordActivity.this, "Error"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
               }
           }
       });
    }
}