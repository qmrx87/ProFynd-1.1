package com.example.profynd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ActivityNavigatorDestinationBuilderKt;

import com.theartofdev.edmodo.cropper.CropImage;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddPostActivity extends AppCompatActivity {
    Button publish ;
    Dialog dialog;
    private EditText titleEditTxt, bodyEditTxt,priceEditTxt;
    private ImageButton returnButton;
    TextInputEditText available_places;
    private Button postButton,okay,addimage;
    private CircleImageView profileImg;
    RadioGroup radioGroup ;
    public String title,priceString,body;
    ProgressBar progressbarokay;
    private CheckBox free;
    int places ;
    private Integer price;

    private FirebaseFirestore fstore;
    private DocumentReference teacherRef;
    private FirebaseAuth auth;
    private FirebaseUser user;


    private Uri mImageUri;
    private ProgressDialog loader ;
    private String imageUrl ="";
    private String onlineUserId ="";
    private String tutorName,tutorUsername,tutorLocation ,downloadUrl;
    private ImageView formationimg;
    private ArrayList<String> selectedTags = new ArrayList<String>();

    private Chip primary_school, middle_school, high_school,
            programming,english,security,networking,uiux,french,spain,
            mathematics,physics,science,languages ,other;

    private static final int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        publish=findViewById(R.id.postBtn);
        titleEditTxt = findViewById(R.id.titleEditTxt);
        priceEditTxt=findViewById(R.id.priceEditTxt);
        bodyEditTxt = findViewById(R.id.bodyEditTxt);
        returnButton = findViewById(R.id.returnBtn);
        postButton = findViewById(R.id.postBtn);
        profileImg = findViewById(R.id.profileImg);
        free=findViewById(R.id.free);
        InitChips();
        loader = new ProgressDialog(this);
        dialog = new Dialog(AddPostActivity.this);
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        onlineUserId = user.getUid();
        teacherRef = fstore.collection("Users").document(onlineUserId);
        //get tutor informations
        teacherRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(this.toString(), "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    tutorUsername = snapshot.get("Username").toString();
                    tutorLocation = snapshot.get("Location").toString();
                    if (snapshot.get("profilePictureUrl") != null) {
                        downloadUrl = snapshot.get("profilePictureUrl").toString();
                        Glide.with(getApplicationContext()).load(downloadUrl).into(profileImg);
                    }
                    if (snapshot.get("Name")!= null)
                    {
                        tutorName = snapshot.get("Name").toString();
                    }


                }
                else {
                    Log.d(this.toString(), "Current data: null");
                }
            }
        });
        free.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    priceEditTxt.setEnabled(false);
                    priceEditTxt.setText("Free");
                } else {
                    priceEditTxt.setEnabled(true);
                    priceEditTxt.setText("");
                    priceEditTxt.setHint("Formation Price");
                }
            }
        });
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        SetCheckedTags();
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTags.size()<1)
                {
                    Toast.makeText(AddPostActivity.this, "Please select atleast one tag", Toast.LENGTH_SHORT).show();
                    dialog.hide();
                    return;
                }
                 title = titleEditTxt.getText().toString().trim();
                 priceString = priceEditTxt.getText().toString().trim();
                 body = bodyEditTxt.getText().toString().trim();

                if (TextUtils.isEmpty(title)) {
                    titleEditTxt.setError("Title is required!");
                    titleEditTxt.requestFocus();
                    return;
                } else if(!free.isChecked()){

                    if(TextUtils.isEmpty(priceString)){
                        priceEditTxt.setError("Price is required!");
                        priceEditTxt.requestFocus();
                        return;
                    }
                    if (!TextUtils.isDigitsOnly(priceString)) {
                        priceEditTxt.setError("Price must be a number!");
                        priceEditTxt.requestFocus();
                        return;

                }else{
                        price = Integer.parseInt(priceString);

                    }
                } else if (TextUtils.isEmpty(body)) {
                    bodyEditTxt.setError("Body is required!");
                    bodyEditTxt.requestFocus();
                    return;
                }

                dialog.setContentView(R.layout.addformation_dialog);
                dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                progressbarokay=dialog.findViewById(R.id.progressbarokay);
                dialog.show();
                progressbarokay.setVisibility(View.INVISIBLE);
                    okay = dialog.findViewById(R.id.okay);
                    addimage=dialog.findViewById(R.id.add_img);
                    available_places = dialog.findViewById(R.id.available_places);
                    available_places.setEnabled(false);
                    radioGroup = dialog.findViewById(R.id.radioGroup);
                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            if (checkedId == R.id.collective) {
                                available_places.setEnabled(true);
                                available_places.setError(null);
                                available_places.requestFocus();
                            } else {
                                available_places.setEnabled(false);
                                available_places.setError(null);
                                available_places.setText("");
                            }
                        }
                    });

                okay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        okay.setEnabled(false);
                        RadioButton individual = dialog.findViewById(R.id.individual);
                        RadioButton collective = dialog.findViewById(R.id.collective);

                        if (radioGroup.getCheckedRadioButtonId() == -1) {
                            // No radio button is checked, show a toast
                            Toast.makeText(AddPostActivity.this, "Please select a formation type", Toast.LENGTH_SHORT).show();

                        } else if (individual.isChecked()) {
                            // If the individual radio button is selected, disable the available_places EditText

                            available_places.setEnabled(false);

                            if (mImageUri != null) {
                                progressbarokay.setVisibility(View.VISIBLE);
                                uploadImageToFirestore(mImageUri);

                            } else {
                                progressbarokay.setVisibility(View.VISIBLE);
                                performValidation("");

                            }
                        } else if (collective.isChecked()) {
                            // If the collective radio button is selected, check if the available_places EditText is empty
                            String availabePlacesString = available_places.getText().toString().trim();
                            if (TextUtils.isEmpty(availabePlacesString)) {
                                available_places.setError("Please Enter places");
                                available_places.requestFocus();
                            } else if (!TextUtils.isDigitsOnly(availabePlacesString)) {
                                available_places.setError("Please enter a valid number");
                                available_places.requestFocus();
                            } else {
                                places = Integer.parseInt(availabePlacesString);
                                // If the user has selected an image, upload it to Firebase Storage before hiding the dialog
                                if (mImageUri != null) {
                                    progressbarokay.setVisibility(View.VISIBLE);
                                    uploadImageToFirestore(mImageUri);
                                } else {
                                    progressbarokay.setVisibility(View.VISIBLE);
                                    performValidation("");
                                }
                            }
                        }

                    }
                });

                addimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch the system file picker to allow the user to select an image
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, PICK_IMAGE_REQUEST);
                    }
                });



            }



        });



    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            // Launch the image cropper
            CropImage.activity(selectedImageUri)
                    .setAspectRatio(16, 9)  // Set your desired custom ratio here
                    .setFixAspectRatio(true) // Ensure the aspect ratio is maintained during cropping
                    .start(this);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            // Handle the result of the crop activity
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                // Get the cropped image URI
                Uri croppedImageUri = result.getUri();


                // Upload the cropped image to Firestore
                uploadImageToFirestore(croppedImageUri);
                addimage.setText("img_05062022");
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                // Handle crop error
                Exception error = result.getError();
                Log.e("TAG", "Error cropping image", error);
            }
        }
    }

    // Upload the cropped image to Firestore
    private void uploadImageToFirestore(Uri croppedImageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("formationImages/" + UUID.randomUUID().toString());

        storageRef.putFile(croppedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get the download URL of the uploaded image and do something with it, such as store it in a Firestore document
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUrl = uri.toString();
                        okay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                performValidation(imageUrl);
                            }
                        });



                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle the upload failure here
                Log.e("AddPostActivity", "Error uploading image", e);
            }
        });
    }




    DocumentReference ref = FirebaseFirestore.getInstance().collection("Posts").document();

    private void performValidation(String img)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        java.util.Calendar c = Calendar.getInstance();
        c.setTime(date);


        startLoader();
        String postId = ref.getId();
        HashMap<String, Object> data = new HashMap<>();
        data.put("Date", date);
        data.put("Formation_img", img);
        data.put("Name", tutorName);
        data.put("Places", places);
        data.put("Price",(free.isChecked())?price:0);
        data.put("Username", tutorUsername);
        data.put("body", body);
        data.put("demands", null);
        data.put("demandsCount", 0);
        data.put("location", tutorLocation);
        data.put("postid", postId);
        data.put("publisher", onlineUserId);
        data.put("PublisherPic",downloadUrl);
        data.put("reportsCount", 0);
        data.put("tags",selectedTags);
        data.put("title", title);







        CollectionReference formationsRef = FirebaseFirestore.getInstance()
                .collection("Users").document(onlineUserId).collection("Formations");

        DocumentReference df = fstore.collection("Posts").document(postId);
        df.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    formationsRef.add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            Toast.makeText(AddPostActivity.this, "Formation posted successfully", Toast.LENGTH_SHORT).show();
                            loader.dismiss();
                            startActivity(new Intent(getApplicationContext(), BottomNavigationActivity.class));
                            finish();
                        }
                    });


                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddPostActivity.this, "Could not post question " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                loader.dismiss();
            }
        });
    }

    //getSelectedTags

    private void SetCheckedTags()
    {
        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    selectedTags.add(compoundButton.getText().toString());
                }
                else
                {
                    selectedTags.remove(compoundButton.getText().toString());
                }
            }
        };

        primary_school.setOnCheckedChangeListener(checkedChangeListener);
        middle_school.setOnCheckedChangeListener(checkedChangeListener);
        high_school.setOnCheckedChangeListener(checkedChangeListener);
        programming.setOnCheckedChangeListener(checkedChangeListener);
        english.setOnCheckedChangeListener(checkedChangeListener);
        security.setOnCheckedChangeListener(checkedChangeListener);
        networking.setOnCheckedChangeListener(checkedChangeListener); // i love programming v2
        uiux.setOnCheckedChangeListener(checkedChangeListener);
        french.setOnCheckedChangeListener(checkedChangeListener);
        spain.setOnCheckedChangeListener(checkedChangeListener);
        mathematics.setOnCheckedChangeListener(checkedChangeListener);
        physics.setOnCheckedChangeListener(checkedChangeListener);
        science.setOnCheckedChangeListener(checkedChangeListener);
        other.setOnCheckedChangeListener(checkedChangeListener);
        languages.setOnCheckedChangeListener(checkedChangeListener);

    }
    private void InitChips() {

        primary_school = findViewById(R.id.Primary_School);
        middle_school = findViewById(R.id.Middle_School);
        high_school = findViewById(R.id.High_School);
        programming = findViewById(R.id.Programming);
        english = findViewById(R.id.English);
        security = findViewById(R.id.Security);
        networking = findViewById(R.id.Networking);
        uiux = findViewById(R.id.ui_ux);
        french = findViewById(R.id.Frensh);
        spain = findViewById(R.id.Spain);
        mathematics = findViewById(R.id.Mathematics);
        physics = findViewById(R.id.Physics);
        science = findViewById(R.id.Science);
        other = findViewById(R.id.other);
        languages = findViewById(R.id.Languages);

    }

    //check network
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void startLoader()
    {
        loader.setMessage("Publishing...");
        loader.setCanceledOnTouchOutside(false);
        loader.show();
    }


}