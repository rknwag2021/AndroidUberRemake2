package com.example.uber;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;

public class SplashScreenActivity extends AppCompatActivity {
    private final static int LOGIN_REQUEST_CODE=7171;
    private List<AuthUI.IdpConfig> providers;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    FirebaseDatabase database;



    @Override
    protected void onStart()
    {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);
    }
    @Override
    protected void onStop()
    {
        if(firebaseAuth!=null & listener !=null)
            firebaseAuth.removeAuthStateListener(listener);
        super.onStop();

    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        init();
    }

    private void init() {
        providers= Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        firebaseAuth=FirebaseAuth.getInstance();
        listener=myFirebaseAuth ->{
            FirebaseUser user=myFirebaseAuth.getCurrentUser();
            if(user!=null)
                delaySplashScreen();
            else
                showLoginLayout();
        };

    }

    private void showLoginLayout() {
        AuthMethodPickerLayout authMethodPickerLayout=new AuthMethodPickerLayout
                .Builder(R.layout.layout_sign_in)
                .setPhoneButtonId(R.id.btn_phone_sign_in)
                .setGoogleButtonId(R.id.btn_google_sign_in)
                .build();
        startActivityForResult(AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAuthMethodPickerLayout(authMethodPickerLayout)
        .setIsSmartLockEnabled(false)
                .setAvailableProviders(providers)
                .build(),LOGIN_REQUEST_CODE

        );



    }
    private void showRegisterForm()
    {



        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.DialogTheme);
        View itemView = LayoutInflater.from(this).inflate(R.layout.layout_sign_up, null);

        TextInputEditText edt_first_name = (TextInputEditText)itemView.findViewById(R.id.edt_first_name);
        TextInputEditText edt_last_name = (TextInputEditText)itemView.findViewById(R.id.edt_last_name);
        TextInputEditText edt_phone=(TextInputEditText)itemView.findViewById(R.id.edt_phone_number);
        TextInputEditText edt_address=(TextInputEditText)itemView.findViewById(R.id.edt_address);

        Button btn_continue = (Button)itemView.findViewById(R.id.btn_register);
        builder.setView(itemView);
        AlertDialog dialog = builder.create();
        dialog.show();
        Toast.makeText(SplashScreenActivity.this,"in REGISTER FORM  "+FirebaseAuth.getInstance().getCurrentUser().getUid(),Toast.LENGTH_SHORT).show();

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String first_name = edt_first_name.getText().toString();
                String last_name=edt_last_name.getText().toString();
                String mobile_no = edt_phone.getText().toString();
                String address=edt_address.getText().toString();
                if (first_name.isEmpty()){
                    Toast.makeText(SplashScreenActivity.this,"Pleas fill the FIRST NAME field",Toast.LENGTH_SHORT).show();
                }
                else if(last_name.isEmpty())
                {

                        Toast.makeText(SplashScreenActivity.this,"Pleas fill the LAST NAME field",Toast.LENGTH_SHORT).show();

                }
                else if(address.isEmpty())
                {

                    Toast.makeText(SplashScreenActivity.this,"Pleas fill the ADDRESS field",Toast.LENGTH_SHORT).show();

                }
                else if(mobile_no.isEmpty())
                {

                        Toast.makeText(SplashScreenActivity.this,"Pleas fill the PHONE NUMBER field",Toast.LENGTH_SHORT).show();


                }
                else
                {
                    String Colector = first_name + "\n";
                    Colector+=last_name+"\n";
                    Colector+=mobile_no+"\n";
                    Toast.makeText(SplashScreenActivity.this,"User Info \n:"+Colector,Toast.LENGTH_SHORT).show();
                    // NOTE - WE HAVE TO REMOVE THIS COLLECTOR

                    database =  FirebaseDatabase.getInstance();
                    DatabaseReference mRef =  database.getReference().child("Users").push();
                    FirebaseUser user =  firebaseAuth.getCurrentUser();
                    FirebaseUser name =  firebaseAuth.getCurrentUser();
                    FirebaseUser phone =  firebaseAuth.getCurrentUser();
                    FirebaseUser work_address =  firebaseAuth.getCurrentUser();

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    mRef.child("user").child("userId").setValue(userId);
                    mRef.child("user").child("name").setValue(first_name);
                    mRef.child("user").child("phone").setValue(mobile_no);
                    mRef.child("user").child("work_address").setValue(address);

                   startActivity(new Intent(SplashScreenActivity.this, Permission_Activity.class));




                }







            }
        });





    }

    private void delaySplashScreen()
    {
        Completable.timer(2, TimeUnit.SECONDS,
                AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        Toast.makeText(SplashScreenActivity.this,"Welcome: "+FirebaseAuth.getInstance().getCurrentUser().getUid(),Toast.LENGTH_SHORT).show();

                        showRegisterForm();
                    }
                });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==LOGIN_REQUEST_CODE)
        {
            IdpResponse response=IdpResponse.fromResultIntent(data);
            if(resultCode==RESULT_OK)
            {
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
            }
            else
            {
                Toast.makeText(this , "[ERROR]:"+response.getError().getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }
}