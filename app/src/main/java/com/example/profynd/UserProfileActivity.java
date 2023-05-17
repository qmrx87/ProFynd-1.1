package com.example.profynd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.profynd.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {
    private TextView usernameTxt,name, bio, reputationText;
    private CircleImageView profilePic;
    private UserModel userModel, currentUserModel;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private DocumentReference requestedByStud;
    private String studUsername,studName;
    private String downloadUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        profilePic = findViewById(R.id.profilePic);
        name = findViewById(R.id.profileName);
        reputationText = findViewById(R.id.reputationText);
        usernameTxt = findViewById(R.id.usernameTxt);
        bio = findViewById(R.id.profileBio);
        userModel = (UserModel) getIntent().getSerializableExtra("Tag");
        DocumentReference userRef = fstore.collection("Users").document(userModel.getUid());
        DocumentReference currentUserRef = fstore.collection("Users").document(user.getUid());
        //Get Name and Username of the online user//
        requestedByStud = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());
        requestedByStud.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(this.toString(), "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    studUsername = snapshot.get("Username").toString();
                    if (snapshot.get("profilePictureUrl") != null) {
                        downloadUrl = snapshot.get("profilePictureUrl").toString();
                    }
                    if (snapshot.get("Name")!= null)
                    {
                        studName = snapshot.get("Name").toString();
                    }
                }
                else {
                    Log.d(this.toString(), "Current data: null");
                }
            }
        });

        SetUserInfo();
    }
    private void SetUserInfo()
    {
        usernameTxt.setText("@"+userModel.getUsername());
        name.setText(userModel.getName());
        bio.setText(userModel.getBio());
        reputationText.setText(userModel.getReputation()+"");
        Glide.with(UserProfileActivity.this).load(userModel.getProfilePictureUrl()).into(profilePic);
    }
    Date requestDate = new Date();

    //*** Set Request data in Firebase ***//
    private void PerformValidation(String question){
        userModel = (UserModel) getIntent().getSerializableExtra("Tag");
        DocumentReference requestRef = FirebaseFirestore.getInstance().collection("Users").document(userModel.getUid())
                .collection("Requests").document();
        String requestId = requestRef.getId();
        Toast.makeText(UserProfileActivity.this, "Sending...", Toast.LENGTH_SHORT).show();
        HashMap<String, Object> data = new HashMap<>();

        data.put("RequestId",requestId);
        data.put("Question",question);
        data.put("Name",studName);
        data.put("Username",studUsername);
        data.put("Date",requestDate);
        data.put("ProfilePictureUrl",downloadUrl);
        data.put("Uid",user.getUid());

        DocumentReference df = fstore.collection("Users").document(userModel.getUid())
                .collection("Requests").document(requestId);
        df.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(UserProfileActivity.this, "Question has been sent successfully", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(UserProfileActivity.this, "Could not send question " + task.getException().toString(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}