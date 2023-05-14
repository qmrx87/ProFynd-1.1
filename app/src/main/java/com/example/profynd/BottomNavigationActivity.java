package com.example.profynd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.profynd.models.UserModel;
import com.example.profynd.navigation_fragments.HomeFragment;
import com.example.profynd.navigation_fragments.NotificationFragment;
import com.example.profynd.navigation_fragments.ProfileFragment;
import com.example.profynd.navigation_fragments.SettingsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class BottomNavigationActivity extends AppCompatActivity  {
    FirebaseAuth mAuth =FirebaseAuth.getInstance() ;
    FirebaseFirestore fstore= FirebaseFirestore.getInstance();
    FirebaseUser user ;
    String userType;
    UserModel currUser ;
    ImageView formation_Img ;
    BottomNavigationItemView add;
    BottomNavigationView bottomNav ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);
        //Set the menu of bottom navigation
        bottomNav = findViewById(R.id.bottom_navigation_layout);
        user=mAuth.getCurrentUser();
         fstore.collection("Users").document(user.getUid()).get().addOnSuccessListener(documentSnapshot -> {
           userType= documentSnapshot.getString("Type");
            int menuResId = (userType.equals("Student")) ? R.menu.stud_bottom_navigation : R.menu.prof_bottom_navigation;
            bottomNav.getMenu().clear();
            bottomNav.inflateMenu(menuResId);
                     if (userType.equals("Student")) {
                         getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                 new HomeFragment()).commit();//Here we're setting the home fragment as default fragment for the student
                     }else {
                         getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                 new ProfileFragment()).commit();
                         //start AddPostActivity class
                         add = findViewById(R.id.nav_add);
                         add.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View view) {
                                 startActivity(new Intent(getApplicationContext(), AddPostActivity.class));
                             }
                         });
                     }//Here we're setting the profile fragment as default fragment for the tutor
        });
        //Here we're setting the home fragment as default fragment





        //get current user model
        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();


        CheckNetwork();

        user.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if (user == null) {
                    startActivity(new Intent(BottomNavigationActivity.this, LoginActivity.class));
                    finish();
                } else if (!user.isEmailVerified()) {
                    startActivity(new Intent(BottomNavigationActivity.this, Verification_Activity.class));
                    finish();
                }
            }
        });


        bottomNav.setVisibility(View.INVISIBLE);

        DocumentReference df = fstore.collection("Users").document(user.getUid());
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        currUser = doc.toObject(UserModel.class);
                        bottomNav.setVisibility(View.VISIBLE);
                    }
                }
            }
        });





    }

    private void CheckNetwork() {
        if (!isNetworkAvailable()) {
            View parentLayout = findViewById(android.R.id.content);
            Snackbar snackbar = Snackbar.make(parentLayout, "⚠️ Please check you internet connection and try again.", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("REFRESH", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckNetwork();
                }
            }).show();
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private BottomNavigationView.OnItemSelectedListener navListener =
            new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_sr:
                            selectedFragment = new SettingsFragment();
                            break;
                        case R.id.nav_notif:
                            selectedFragment = new NotificationFragment();
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };



}