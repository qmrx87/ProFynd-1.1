package com.example.profynd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.profynd.models.PostModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class FormationActivity extends AppCompatActivity {
    TextView titleTextView;
    TextView priceTextView;
    TextView locationTextView;
    TextView bodyTextView;
    TextView placesTextView;
    TextView userTextView;
    ImageView formation_img;
    CircleImageView publisher_img;
    Button add ;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private String useruid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formation);
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        // Retrieve the data from the intent
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("title");
            int price = intent.getIntExtra("Price", 0);
            String location = intent.getStringExtra("location");
            int places = intent.getIntExtra("Places", 0);
            String body = intent.getStringExtra("body");
            String Formation_img =intent.getStringExtra("Formation_img");
            String PublisherPic =intent.getStringExtra("PublisherPic");
            String username = intent.getStringExtra("Username");
            String postid =intent.getStringExtra("postid");
         //   PostModel post =intent.getParcelableExtra("postmodel");

            // Update the UI with the retrieved data
             titleTextView = findViewById(R.id.titletxt);
             priceTextView = findViewById(R.id.pricetxt);
             locationTextView = findViewById(R.id.locationtxt);
            bodyTextView = findViewById(R.id.bodytxt);
            placesTextView=findViewById(R.id.placestxt);
            userTextView=findViewById(R.id.user);
            publisher_img=findViewById(R.id.profileImgFor);
            formation_img=findViewById(R.id.formation_img);
            add=findViewById(R.id.add);
            placesTextView.setText((places>0)?String.valueOf(places):"Individual");
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    useruid = user.getUid();

                    if (places >= 1||placesTextView.getText().equals("Individual")) {
                        Toast.makeText(FormationActivity.this, "Your request on the formation has been sent successfully", Toast.LENGTH_SHORT).show();
                        DocumentReference uref = fstore.collection("Users").document(useruid);

                        // Get the post from "Posts" collection
                        DocumentReference postRef = fstore.collection("Posts").document(postid);
                        postRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    // Convert the retrieved document to a PostModel object
                                    PostModel post = documentSnapshot.toObject(PostModel.class);

                                    // Add the post to the current user's "Formations" collection
                                    DocumentReference userFormationsRef = uref.collection("Formations").document(postid);
                                    userFormationsRef.set(post)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Post added to current user's "Formations" collection successfully
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Failed to add the post to current user's "Formations" collection
                                                }
                                            });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to retrieve the post from "Posts" collection
                            }
                        });
                    }
                }
            });


            titleTextView.setText(title);
            priceTextView.setText((price>0)?String.valueOf(price):"Free");
            locationTextView.setText(location);
            bodyTextView.setText(body);
            placesTextView.setText((places>0)?String.valueOf(places):"Individual");
            userTextView.setText("@"+username);
            // Load and display PublisherPic
            Glide.with(this)
                    .load(PublisherPic)
                    .into(publisher_img);

// Load and display Formation_img
            Glide.with(this)
                    .load(Formation_img)
                    .into(formation_img);
        }
    }
}