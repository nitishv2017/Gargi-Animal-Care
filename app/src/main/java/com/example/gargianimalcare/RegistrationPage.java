package com.example.gargianimalcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationPage extends AppCompatActivity {
    RadioGroup radioGroup;
    int flag_code=0;
    TextInputLayout codeEntryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        //call Login with phone activity
        Button regLoginBtn = (Button) findViewById(R.id.regLoginBtn);
        regLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationPage.this , LoginViaEmail.class);
                startActivity(intent);
                finish();
            }
        });
        radioGroup=findViewById(R.id.radio_grp);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i)
                {
                    case R.id.customer_radio:
                        flag_code=0;
                        removecodeView();
                        // TODO Something
                        break;
                    case R.id.admin_radio:
                        flag_code=1;
                        enterCode(flag_code);
                        // TODO Something
                        break;
                    case R.id.employee_radio:
                        flag_code=2;
                        enterCode(flag_code);
                        // TODO Something
                        break;
                }
            }
        });


        //send form data to registration OTP page
        final TextInputEditText phoneNumber,userName,businessName,email,password,entryCode;


        userName = findViewById(R.id.userName_registration);
        businessName = findViewById(R.id.userName_registration);
        email = findViewById(R.id.email_registration);
        phoneNumber = findViewById(R.id.phoneNumber_registration);
        password = findViewById(R.id.password_registration);
        entryCode=findViewById(R.id.codeEnter);
        codeEntryView=findViewById(R.id.code_entry_view);
        findViewById(R.id.registrationBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = userName.getText().toString().trim();
                String businessname = businessName.getText().toString().trim();
                String number = phoneNumber.getText().toString().trim();
                String emails = email.getText().toString().trim();
                String passwordn = password.getText().toString().trim();
                String entryCoden=entryCode.getText().toString().trim();

                if(entryCoden.isEmpty() && flag_code!=0)
                {
                    entryCode.setError("Entry code is required");
                    entryCode.requestFocus();
                    return;
                }

                if(username.isEmpty())
                {
                    userName.setError("Name is required");
                    userName.requestFocus();
                    return;
                }

                if(emails.isEmpty())
                {
                    email.setError("Email is required");
                    email.requestFocus();
                    return;
                }

                if(number.isEmpty() || number.length() < 10)
                {
                    phoneNumber.setError("Valid number is required");
                    phoneNumber.requestFocus();
                    return;
                }

                if(passwordn.isEmpty())
                {
                    password.setError("Enter Password");
                    password.requestFocus();
                    return;
                }
                if(passwordn.length() < 6)
                {
                    password.setError("Atleast 6 Characters");
                    password.requestFocus();
                    return;
                }
                if(flag_code!=0)
                {
                    if(flag_code==1 && entryCoden.equals("12345") )
                    {
                        ;
                    }
                    else if(flag_code==2 && entryCoden.equals("54321"))
                    {
                        ;
                    }
                    else {
                        entryCode.setError("Enter valid entry code");
                        entryCode.requestFocus();
                        return;
                    }
                }

                String numbers = "+" + "91" + number;
                Intent intent = new Intent(RegistrationPage.this,RegistrationOTPPage.class);
                intent.putExtra("username",username);
                intent.putExtra("businessname",businessname);
                intent.putExtra("phonenumber",numbers);
                intent.putExtra("email",emails);
                intent.putExtra("password",passwordn);
                intent.putExtra("flag",flag_code);


                startActivity(intent);
                finish();
            }
        });
    }

    private void removecodeView() {
        codeEntryView.setVisibility(View.INVISIBLE);
    }

    private void enterCode(int flag) {
        if(flag!=0)
        {
            codeEntryView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())
            {
                Intent intent = new Intent(this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }
}
