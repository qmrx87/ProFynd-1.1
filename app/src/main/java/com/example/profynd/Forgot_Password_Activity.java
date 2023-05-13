package com.example.profynd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.intellij.lang.annotations.Pattern;

public class Forgot_Password_Activity extends AppCompatActivity {
    FirebaseFirestore fstore;
    FirebaseAuth auth;
    FirebaseUser user;
    String email;
    private Button next;
    private TextInputEditText emailEditTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        next=findViewById(R.id.sendbtn);
        emailEditTxt = findViewById(R.id.emailchangepassword);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailChangePassword();
            }
        });


    }

    private void sendEmailChangePassword() {
        email = emailEditTxt.getText().toString().trim();
        fstore = FirebaseFirestore.getInstance();

        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            CollectionReference usersRef = fstore.collection("Users");
            usersRef.whereEqualTo("Email", email).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if( !queryDocumentSnapshots.isEmpty()){
                FirebaseAuth.getInstance().sendPasswordResetEmail(email);
                    Toast.makeText(Forgot_Password_Activity.this, "Please check your email and change your password", Toast.LENGTH_SHORT).show();
                    
                }else{
                        Toast.makeText(Forgot_Password_Activity.this, "There is no account with such email", Toast.LENGTH_SHORT).show();

                    }}
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.getMessage();
                }
            })
            ;


        }else{
            emailEditTxt.setError("Valid format of email is required");
        }
    }
}