package com.example.profynd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.profynd.interfaces.GetUserInterface;
import com.example.profynd.models.UserModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BottomNavigationActivity extends AppCompatActivity implements GetUserInterface {
    FirebaseAuth mAuth ;
    FirebaseFirestore fstore;
    FirebaseUser user ;
    UserModel currUser ;
    ImageView formation_Img ;
    BottomNavigationView bottomNav ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);
        bottomNav = findViewById(R.id.bottom_navigation_layout);

        //Set the menu of bottom navigation

         fstore = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = fstore.collection("Users").document(userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            String userType = documentSnapshot.getString("Type");
            int menuResId = userType.equals("Student") ? R.menu.stud_bottom_navigation : R.menu.prof_bottom_navigation;
            bottomNav.inflateMenu(menuResId);
        });







    }



    @Override
    public UserModel getUserModel() {
        return null;
    }
}