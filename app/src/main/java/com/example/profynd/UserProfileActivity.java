package com.example.profynd;

import static android.view.View.VISIBLE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.profynd.adapter.PostAdapter;
import com.example.profynd.interfaces.MainInterface;
import com.example.profynd.interfaces.SearchOnItemClick;
import com.example.profynd.models.PostModel;
import com.example.profynd.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity  implements PostAdapter.PostsOnItemClickListner, MainInterface, SearchOnItemClick {
    private TextView usernameTxt,name, bio, reputationText;
    private CircleImageView profilePic;
    private UserModel userModel, currentUserModel;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private DocumentReference requestedByStud;
    private String studUsername,studName,email,phone;
    private String downloadUrl;
    private BottomSheetDialog dialog;
    private Dialog idialog ;
    private ImageButton askQuestion;
    private CardView emailCV,phoneCV;
    private ProgressBar userProgressBar,progressbarokay1,progressbarokay2;
    private Button copy,call;
    private TextView email_txt,phone_txt;
    private PostAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<PostModel> PostsDataHolder;
    private boolean isLastItemPaged;
    private DocumentSnapshot lastVisible;
    private LinearLayoutManager linearLayoutManager;
    private Boolean isScrolling;
    private DocumentReference userInfos;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        emailCV=findViewById(R.id.emailcardview);
        phoneCV=findViewById(R.id.mobilephonecard);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        fstore = FirebaseFirestore.getInstance();
        profilePic = findViewById(R.id.profileImg);
        name = findViewById(R.id.profileName);
        reputationText = findViewById(R.id.reputationText);
        usernameTxt = findViewById(R.id.usernameTxt);
        bio = findViewById(R.id.profileBio);
        askQuestion=findViewById(R.id.askQuestionBtn);
        recyclerView=findViewById(R.id.userrecview);
        dialog = new BottomSheetDialog(this);
        idialog = new Dialog(this);
        userProgressBar=findViewById(R.id.userProgressBar);
        linearLayoutManager = new LinearLayoutManager(this);
        userModel = (UserModel) getIntent().getSerializableExtra("Tag");
        userInfos = FirebaseFirestore.getInstance().collection("Users").document(userModel.getUid());



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




        GetCurrentUserModel();
        SetUserInfo();
      setUserCourses();

        askQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showQuestionDialog();
                dialog.show();
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

            }
        });

        emailCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idialog.setContentView(R.layout.email_dialog);
                idialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
                idialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                progressbarokay1=idialog.findViewById(R.id.progressbarokay1);
                copy=idialog.findViewById(R.id.copy);
                email_txt=idialog.findViewById(R.id.email_txt);
                email_txt.setText(userModel.getEmail());
                progressbarokay1.setVisibility(View.INVISIBLE);
                idialog.show();

                copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = userModel.getEmail();
                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("Email", email);
                        clipboardManager.setPrimaryClip(clipData);
                        Toast.makeText(UserProfileActivity.this, "Email copied to clipboard", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        phoneCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idialog.setContentView(R.layout.phone_dialog);
                idialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
                idialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                progressbarokay2=idialog.findViewById(R.id.progressbarokay2);
                call=idialog.findViewById(R.id.call);
                phone_txt=idialog.findViewById(R.id.phone_txt);
                phone_txt.setText(userModel.getMobile());
                progressbarokay2.setVisibility(View.INVISIBLE);
                idialog.show();
                call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phoneNumber = userModel.getMobile();
                        Uri phoneUri = Uri.parse("tel:" + phoneNumber);
                        Intent dialIntent = new Intent(Intent.ACTION_DIAL, phoneUri);
                        startActivity(dialIntent);
                    }
                });

            }
        });
    }
   private void setUserCourses(){
       PostsDataHolder = new ArrayList<>();
       isLastItemPaged = false;
       Query query = fstore.collection("Users").document(userModel.getUid()).collection("Formations")
               .orderBy("Date", Query.Direction.DESCENDING)
               .orderBy("demands", Query.Direction.ASCENDING)
               .limit(20);

       query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {
               if (task.isSuccessful()) {
                    Log.e("Task is successful","true");
                   for (QueryDocumentSnapshot document : task.getResult()) {
                       PostModel post = document.toObject(PostModel.class);
                       PostsDataHolder.add(post);
                   }


                   userProgressBar.setVisibility(View.GONE);
                   recyclerView.setVisibility(VISIBLE);
                   if (task.getResult().size() > 0)
                       lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                   BuildRecyclerView();

                  //
               }else{
                   Toast.makeText(UserProfileActivity.this, "NOT SUCCESSFUL", Toast.LENGTH_SHORT).show();
               }
           }
       });
   }

    private void SetUserInfo()
    {
        usernameTxt.setText("@"+userModel.getUsername());
        name.setText(userModel.getName());
        bio.setText(userModel.getBio());
        if (userModel.getType()!="Student") {
            reputationText.setText(userModel.getReputation() + "");
        }else{
            reputationText.setVisibility(View.GONE);
        }
       if(userModel.getProfilePictureUrl()!=null) {
           Glide.with(UserProfileActivity.this).load(userModel.getProfilePictureUrl()).into(profilePic);
       }


    }
    Date requestDate = new Date();


    private void GetCurrentUserModel()
    {
        DocumentReference df = fstore.collection("Users").document(user.getUid());
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists())
                    {
                        currentUserModel =  doc.toObject(UserModel.class);

                    }
                }
            }
        });
    }
    //*** Show Question Dialog ***//
    private void showQuestionDialog(){
        View view = getLayoutInflater().inflate(R.layout.request_dialog,null,false);

        ImageButton closeQuestionBtn = view.findViewById(R.id.closeQuestionBtn);
        EditText askQuestion =  view.findViewById(R.id.sendQuestionEditTxt);
        TextView sendQuestion =  view.findViewById(R.id.sendQuestionBtn);
        CircleImageView profileimg = view.findViewById(R.id.questionBottomsheetImg);

        Glide.with(UserProfileActivity.this).load(downloadUrl).into(profileimg);

        closeQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        sendQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!askQuestion.getText().toString().isEmpty())
                {
                    PerformValidation(askQuestion.getText().toString());
                    dialog.dismiss();
                }else {
                    askQuestion.setError("Enter your question");
                }
            }
        });
        dialog.setContentView(view);
    }
    private void BuildRecyclerView() {
        recyclerView = findViewById(R.id.userrecview);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new PostAdapter(PostsDataHolder, this);
        recyclerView.setAdapter(adapter);
    }
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


    public void Notify(Task<DocumentSnapshot> publisherTask, String title, Activity activity){
        DocumentReference userRef = fstore.collection("Users").document(userModel.getUid());

       /* fstore.collection("Users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().getString("Token").equals(publisherTask.getResult().getString("Token"))) {
                        FcmNotificationsSender send = new FcmNotificationsSender(
                                publisherTask.getResult().getString("Token"),
                                title+" Followed You !",
                                "Click To See All Notifications",
                                UserProfileActivity.this);
                        send.SendNotifications();


                    }
                }
            }
        });*/

        //add notifier data to notified user (name )  ******* this is for the recyclerView **********
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {//userRef is the notified
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    Map<String, Object> notif = new HashMap<>();
                    notif.put("Type", 0);
                    notif.put("UserId",user.getUid());
                    notif.put("Username", title);
                    notif.put("Date", Timestamp.now());
                    notif.put("PostId", null);
                    notif.put("Image", downloadUrl);
                    notif.put("Seen", false);
                    userRef.collection("Notifications")
                            .add(notif); //add the notification data to the notification collection of the notified user

                    userRef.update("unseenNotifications", FieldValue.increment(1));
                }
            }
        });

    }

    @Override
    public void onPictureClick(int position) {

    }

    @Override
    public void onNameClick(int position) {

    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onItemClick(int position, PostModel postModel) {

    }
}