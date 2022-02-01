package com.example.gargianimalcare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class PhoneOTPPage extends AppCompatActivity {

    private String verificationID;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    private EditText enterOTPEditText;
    Button resendOTP;
    ImageView backBtn;
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_otppage);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance("https://gargi-animal-care-default-rtdb.firebaseio.com/");
        reference = firebaseDatabase.getReference("users");

        enterOTPEditText = findViewById(R.id.enterOTPEditText);
        resendOTP=findViewById(R.id.generateNewOTPBtn);
        backBtn=findViewById(R.id.backbtn_phoneOTP);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        phoneNumber = getIntent().getStringExtra("phonenumber");

        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode(phoneNumber);
            }
        });

        sendVerificationCode(phoneNumber);

        //submit button onclick listener
        Button submitOTPBtn = findViewById(R.id.submitLoginOTPBtn);
        submitOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = enterOTPEditText.getText().toString().trim();

                if(code.isEmpty() || code.length() < 6)
                {
                    enterOTPEditText.setError("Enter OTP Code");
                    enterOTPEditText.requestFocus();
                    return;
                }

                verifyCode(code);
            }
        });
    }

    private void verifyCode(String code)
    {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID,code);
        updateCredentials(credential);
    }

    private void updateCredentials(PhoneAuthCredential credential)
    {

        currentUser.updatePhoneNumber(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull  Task<Void> task) {
                if(task.isSuccessful())
                {
                    reference.child(currentUser.getUid()+"/phonenumber").setValue(phoneNumber);
                    Intent intent = new Intent(PhoneOTPPage.this,Account_details.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(PhoneOTPPage.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private void sendVerificationCode(String number)
    {
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                number,
//                120,
//                TimeUnit.SECONDS,
//                this,
//                mCallBack
//
//        );
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationID = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();

            if(code != null)
            {
                verifyCode(code);
            }

            if(code == null)
            {
                updateCredentials(phoneAuthCredential);
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(PhoneOTPPage.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
}
