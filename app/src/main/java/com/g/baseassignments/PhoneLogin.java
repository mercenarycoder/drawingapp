package com.g.baseassignments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLogin extends AppCompatActivity {
ScrollView contrycd;
EditText editTextPhone,digit_1,digit_2,digit_3,digit_4,digit_5,digit_6;
TextView buttonGet,buttonSignIn,some_number;
LinearLayout after_number;
String number="";
Context context;
private static final String TAG = "PhoneAuthActivity";

private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

private static final int STATE_INITIALIZED = 1;
private static final int STATE_CODE_SENT = 2;
private static final int STATE_VERIFY_FAILED = 3;
private static final int STATE_VERIFY_SUCCESS = 4;
private static final int STATE_SIGNIN_FAILED = 5;
private static final int STATE_SIGNIN_SUCCESS = 6;

    // [START declare_auth]
private FirebaseAuth mAuth;
    // [END declare_auth]

private boolean mVerificationInProgress = false;
private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
private String mVerificationId;
private PhoneAuthProvider.ForceResendingToken mResendToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=PhoneLogin.this;
        mAuth=FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null)
        {
            Intent intent=new Intent(context,MainActivity.class);
            intent.putExtra("id",mAuth.getUid());
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_phone_login);
        digit_1=(EditText)findViewById(R.id.digit_1);
        digit_2=(EditText)findViewById(R.id.digit_2);
        digit_3=(EditText)findViewById(R.id.digit_3);
        digit_4=(EditText)findViewById(R.id.digit_4);
        digit_5=(EditText)findViewById(R.id.digit_5);
        digit_6=(EditText)findViewById(R.id.digit_6);
        digit_1.addTextChangedListener(new watcher(digit_1));
        digit_2.addTextChangedListener(new watcher(digit_2));
        digit_3.addTextChangedListener(new watcher(digit_3));
        digit_4.addTextChangedListener(new watcher(digit_4));
        digit_5.addTextChangedListener(new watcher(digit_5));
        digit_6.addTextChangedListener(new watcher(digit_6));
        buttonGet=(TextView)findViewById(R.id.buttonGet);
        buttonSignIn=(TextView)findViewById(R.id.buttonSignIn);
        contrycd=(ScrollView)findViewById(R.id.countrycd);
        after_number=(LinearLayout)findViewById(R.id.after_number);
        some_number=(TextView)findViewById(R.id.some_number);
        editTextPhone=(EditText)findViewById(R.id.editTextPhone);
        buttonGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number=editTextPhone.getText().toString();
                if(number.length()==10)
                {
                    Toast.makeText(context,"right Number",Toast.LENGTH_SHORT).show();
                    contrycd.setVisibility(View.INVISIBLE);
                    some_number.setText(number);
                    number="+91"+number;
                    resendVerificationCode(number,mResendToken);
                }
                else
                {
                    Toast.makeText(context,"Please Enter the correct number",Toast.LENGTH_SHORT).show();
                }
            }
        });
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               verifySignInCode();
            }
        });
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Toast.makeText(PhoneLogin.this, "Auto Verification Occurring", Toast.LENGTH_SHORT).show();

                mVerificationInProgress = false;
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                mVerificationInProgress = false;
             Toast.makeText(context,"Verification failed",Toast.LENGTH_SHORT).show();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
             Toast.makeText(context,"Invalid Firebase Credentials",Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // Save verification ID and resending token so we can use them later
                Toast.makeText(PhoneLogin.this, "Code is being Sent hurry", Toast.LENGTH_SHORT).show();
                mVerificationId = verificationId;
                mResendToken = token;
       }
        };

    }

    private void verifySignInCode(){

        String code ="";
        int i=0;
        switch(i)
        {
            case 0:
                code+=digit_1.getText().toString();
            case 1:
                code+=digit_2.getText().toString();
            case 3:
                code+=digit_3.getText().toString();
            case 4:
                code+=digit_4.getText().toString();
            case 5:
                code+=digit_5.getText().toString();
            case 6:
                code+=digit_6.getText().toString();
                break;
        }
        if(code.length()<6)
        {
            Toast.makeText(PhoneLogin.this,"Code should be of length 6",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(code)){
            Toast.makeText(PhoneLogin.this, "Please enter code", Toast.LENGTH_SHORT).show();
            //waitingDialog.dismiss();
        }
        else {
            Toast.makeText(PhoneLogin.this, "It Reched Here", Toast.LENGTH_SHORT).show();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            signInWithPhoneAuthCredential(credential);
        }
    }
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            Intent intent=new Intent(context,MainActivity.class);
                            intent.putExtra("phone",user.getPhoneNumber());
                            intent.putExtra("id",user.getUid());
                            startActivity(intent);
                            finish();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                           Toast.makeText(context,"Please Enter a Valid OTP",Toast.LENGTH_SHORT).show();
                           contrycd.setVisibility(View.VISIBLE);
                            }
                            // [START_EXCLUDE silent]
                            // Update UI
                            //updateUI(STATE_SIGNIN_FAILED);
                            // [END_EXCLUDE]
                        }
                    }
                });
    }
    // [END sign_in_with_phone]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        Toast.makeText(context,"Code is sending",Toast.LENGTH_SHORT).show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
        System.out.println("Reaching Herere ---------------------------------------------------------");
    }
    private class  watcher implements TextWatcher {
        View view;
        public watcher(View view)
        {
            this.view=view;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            switch (view.getId()) {
                case R.id.digit_1: {
                    if (text.length() == 1) {
                        digit_2.requestFocus();
                    }
                    break;
                }
                case R.id.digit_2: {
                    if (text.length() == 1) {
                        digit_3.requestFocus();
                    } else if (text.length() == 0) {
                        digit_1.requestFocus();
                    }
                    break;
                }
                case R.id.digit_3: {
                    if (text.length() == 1) {
                        digit_4.requestFocus();
                    } else if (text.length() == 0) {
                        digit_3.requestFocus();
                    }
                    break;
                }
                case R.id.digit_4: {
                    if (text.length() == 1) {
                        digit_5.requestFocus();
                    } else if (text.length() == 0) {
                        digit_4.requestFocus();
                    }
                    break;
                }
                case R.id.digit_5: {
                    if (text.length() == 1) {
                        digit_6.requestFocus();
                    } else if (text.length() == 0) {
                        digit_4.requestFocus();
                    }
                    break;
                }
                case R.id.digit_6: {
                    if (text.length() == 0) {
                        digit_5.requestFocus();
                    }
                    break;
                }
            }
        }
    }
}
