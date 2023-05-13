package com.example.profynd;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class register_activity extends AppCompatActivity {
    private Button teacher, student, registerBtn;
    TextInputEditText usernameEditTxt, emailEditTxt, pwEditTxt, mobEditTxt;
    private boolean tIsClicked, sIsClicked;
    FirebaseFirestore fstore;
    ProgressBar progressBar;
    boolean valid = false;
    Date date = new Date();
    private static final int REQUEST_CODE =1;
    private static final String TAG = "register_activity";
    private static int REQUEST_LOCATION_PERMISSION;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private Geocoder mGeocoder;
    private EditText mLocationInput;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_layout);


        fstore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

       //take location
        mLocationInput = findViewById(R.id.localisation1);
        mLocationInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                getLastLocation();
            }});
        registerUser();
        //open sign_in Layout
        TextView sign_in = findViewById(R.id.signin);
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sign_in = new Intent(register_activity.this, login_activity.class);
                startActivity(sign_in);
            }
        });
        //Declaration of Switcher Teacher/Student
        teacher = findViewById(R.id.teacher);
        student = findViewById(R.id.student);
        tIsClicked = false;
        sIsClicked = false;
        //Select teacher
        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tIsClicked) {
                    teacher.setBackgroundResource(R.drawable.button_backround);
                    teacher.setTextColor(getColor(R.color.white));
                    tIsClicked = true;
                    student.setBackgroundResource(R.drawable.unselected_button);
                    student.setTextColor(getColor(R.color.PrimCol));
                    sIsClicked = false;
                } else {
                    teacher.setBackgroundResource(R.drawable.unselected_button);
                    teacher.setTextColor(getColor(R.color.PrimCol));
                    tIsClicked = false;

                }

            }
        });
        //Select student
        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sIsClicked) {
                    student.setBackgroundResource(R.drawable.button_backround);
                    student.setTextColor(getColor(R.color.white));
                    sIsClicked = true;
                    teacher.setBackgroundResource(R.drawable.unselected_button);
                    teacher.setTextColor(getColor(R.color.PrimCol));
                    tIsClicked = false;

                } else {
                    student.setBackgroundResource(R.drawable.unselected_button);
                    student.setTextColor(getColor(R.color.PrimCol));
                    sIsClicked = false;
                }

            }
        });







    }

    //Registration method
    private void registerUser() {
        //Register User

        usernameEditTxt = findViewById(R.id.UsernameEditTxt);
        emailEditTxt = findViewById(R.id.EmailEditTxt);
        pwEditTxt = findViewById(R.id.PasswordEditTxt);
        mobEditTxt = findViewById(R.id.MobileEditTxt);
        registerBtn = findViewById(R.id.SignUpButton);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);
        //Handling exceptions
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditTxt.getText().toString().trim();
                String password = pwEditTxt.getText().toString().trim();
                String username = usernameEditTxt.getText().toString().trim();
                String mobile = mobEditTxt.getText().toString().trim();
                String localisation = mLocationInput.getText().toString();
                boolean account_type = sIsClicked;


                //username processing
                if (TextUtils.isEmpty(username)) {
                    usernameEditTxt.setError("Username is required!");
                    usernameEditTxt.requestFocus();
                    progressBar.setVisibility(View.INVISIBLE);

                    return;
                } else

                    //regular expression to validate username
                    if (!username.matches("^[a-zA-Z0-9._-]{3,}$")) {
                        usernameEditTxt.setError("Please enter a valid username");
                        usernameEditTxt.requestFocus();
                        progressBar.setVisibility(View.INVISIBLE);

                    } else

                        //email processing
                        if (TextUtils.isEmpty(email)) {
                            emailEditTxt.setError("Email is required!");
                            emailEditTxt.requestFocus();
                            progressBar.setVisibility(View.INVISIBLE);

                            return;
                        } else
                            //Email Valid Format
                            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                Toast.makeText(register_activity.this, "Please enter a valid format of EMAIL", Toast.LENGTH_SHORT).show();
                                emailEditTxt.setError("Valid Email format is required!");
                                emailEditTxt.requestFocus();
                                progressBar.setVisibility(View.INVISIBLE);

                            }

                //pw processing block
                if (TextUtils.isEmpty(password)) {
                    pwEditTxt.setError("Password is required!");
                    pwEditTxt.requestFocus();
                    progressBar.setVisibility(View.INVISIBLE);



                } else
                    //Password too weak
                    if (password.length() < 6) {
                        Toast.makeText(register_activity.this, "Password is too weak", Toast.LENGTH_SHORT).show();
                        pwEditTxt.setError("Password  must be more than 6 characters!");
                        pwEditTxt.requestFocus();
                        progressBar.setVisibility(View.INVISIBLE);

                    } else if (!sIsClicked && !tIsClicked) {
                        Toast.makeText(register_activity.this, "PLEASE Select an account type", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);

                    } else if(TextUtils.isEmpty(mLocationInput.getText())){

                        Toast.makeText(register_activity.this, "PLEASE Enter your Location in this format Country , City", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }else
                    if(CheckExistingUser(username))

                    //Start the Registration
                    {
                        progressBar.setVisibility(View.VISIBLE);


                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                                new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            //get the current user
                                            user = auth.getCurrentUser();

                                            //store user id into firebase

                                            Map<String, Object> userInfor = new HashMap<>(); //represents key, value
                                            //can be used to categorise our data and organize it
                                            userInfor.put("Username", username);
                                            userInfor.put("Email", email); //email categorie
                                            userInfor.put("Mobile", mobile);
                                            userInfor.put("Location", localisation);
                                            userInfor.put("Uid", user.getUid());
                                            userInfor.put("Type", account_type);
                                            userInfor.put("Reputation", 0);
                                            userInfor.put("ProfilePictureUrl", null);

                                            //specify access level (if user is admin)
                                            userInfor.put("isAdmin", false);

                                            DocumentReference dr = fstore.collection("Users").document(user.getUid());

                                           dr.set(userInfor).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    user.sendEmailVerification();
                                                    Intent intent = new Intent(register_activity.this,Verification_Activity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(register_activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    Log.e(" df.set(userInfor) Task", "Failure");
                                                }
                                            }); //pass our map to the fb document
                                        } else try {
                                            Log.e("Registration of User with Email and Password","Failure");
                                            throw task.getException();

                                        } catch (FirebaseAuthUserCollisionException e) {
                                            emailEditTxt.setError("Email is already registered , Please enter anotehr one");
                                        } catch (FirebaseAuthEmailException e) {
                                            emailEditTxt.setError("This Email is Banned/Used");
                                            task.getException().getMessage();
                                        } catch (FirebaseAuthInvalidUserException e) {
                                            e.getMessage();
                                        } catch (Exception e) {
                                            Log.e(TAG, "Exception e , task not successful");
                                            Toast.makeText(register_activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                        );
                    }
            }


        });

    }

    private boolean CheckExistingUser(String name) {
        //get all users
        fstore.collection("Users").whereEqualTo("Username", name)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    //   Toast.makeText(getApplicationContext(), "method clicked", Toast.LENGTH_SHORT).show();
                    if (task.getResult().size() > 0) {
                        valid = false;
                        progressBar.setVisibility(View.GONE);
                        usernameEditTxt.setError("Username already exists");
                    } else valid = true;
                } else
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return valid;
    }



    private void retrieveRestoreToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            Map<String, Object> userToken = new HashMap<>();

            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    String token = task.getResult();
                    userToken.put("Token", token);
                    FirebaseFirestore.getInstance().collection("Users").document(
                            FirebaseAuth.getInstance().getCurrentUser().getUid()).update(userToken);
                }
            }
        });
    }
    private void getLastLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


                if (ContextCompat.checkSelfPermission(register_activity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @SuppressLint("SetTextI18n")
                        @Override

                        public void onSuccess(Location location) {




                            if (location != null) {
                                Log.e("Location is not null","Yes");

                                Geocoder geocoder = new Geocoder(register_activity.this, Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    if (addresses.size() > 0) {
                                        String city = addresses.get(0).getLocality();
                                        String country = addresses.get(0).getCountryName();
                                        Log.e("OnSuccess","getLastLocation: location set to " + city + ", " + country);
                                        if (mLocationInput != null) {
                                            mLocationInput.setText(city + ", " + country);
                                            Log.e("OnSuccess","getLastLocation: location set to " + city + ", " + country);
                                        } else {
                                            Log.e("OnSuccess","getLastLocation: mLocationInput is null");
                                        }
                                    }
                                } catch (IOException e) {
                                    e.getMessage();
                                }
                            }else{
                                Log.e("Location is null","Yes");
                                mLocationInput.setText("Algeria , Sidi Bel Abbes");
                            }
                        }
                    });
                } else
                {
                    askPermission();
                }





    }

            private void askPermission() {
                ActivityCompat.requestPermissions(register_activity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            }





        @Override
        public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {

            if (requestCode == REQUEST_CODE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation();
                } else {
                    Toast.makeText(this, "Please provide the requested permission", Toast.LENGTH_SHORT).show();
                }
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }

