package com.example.profynd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.profynd.interfaces.GetUserInterface;
import com.example.profynd.models.UserModel;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class BottomNavigationActivity extends AppCompatActivity  {
    FirebaseAuth mAuth =FirebaseAuth.getInstance() ;
    FirebaseFirestore fstore= FirebaseFirestore.getInstance();
    FirebaseUser user ;
    UserModel currUser ;
    ImageView formation_Img ;
    BottomNavigationView bottomNav ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);
        bottomNav = findViewById(R.id.bottom_navigation_layout);
        user=mAuth.getCurrentUser();
        Task<DocumentSnapshot> type = fstore.collection("Users").document(user.getUid()).get().addOnSuccessListener(documentSnapshot -> {
           String userType= documentSnapshot.getString("Type");
            int menuResId = (userType.equals("Student")) ? R.menu.stud_bottom_navigation : R.menu.prof_bottom_navigation;
            bottomNav.getMenu().clear();
            bottomNav.inflateMenu(menuResId);
        });
        //Set the menu of bottom navigation








    }

    private void setBottomNavigationMenu(String userType) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_layout);
        int menuResId = (userType.equals("student")) ? R.menu.stud_bottom_navigation : R.menu.prof_bottom_navigation;
        bottomNavigationView.getMenu().clear();
        bottomNavigationView.inflateMenu(menuResId);
    }

}