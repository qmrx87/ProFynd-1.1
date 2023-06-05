package com.example.profynd.navigation_fragments;

import static android.view.View.VISIBLE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.profynd.EditProfileActivity;
import com.example.profynd.R;
import com.example.profynd.adapter.PostAdapter;
import com.example.profynd.interfaces.MainInterface;
import com.example.profynd.interfaces.SearchOnItemClick;
import com.example.profynd.models.PostModel;
import com.example.profynd.models.UserModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment implements PostAdapter.PostsOnItemClickListner, MainInterface, SearchOnItemClick {

    private UserModel userModel;
    private ArrayList<String> followers, followings;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private StorageReference storageReference;
    private Uri resultUri;
    private CollectionReference requestRef;

    private DocumentSnapshot lastVisible;
    private PostAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<PostModel> PostsDataHolder;
    private LinearLayoutManager linearLayoutManager;
    private TextView username, name, bio, reputationText;
    private ImageButton editProfile;
    private CollapsingToolbarLayout toolbarLayout;
    private CircleImageView profileImg;
    private ProgressDialog loader;
    private boolean isLastItemPaged;
    private ProgressBar userProgressBar;
    ImageView star;

    View Holder;

    private boolean loadbanner = true;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Holder = inflater.inflate(R.layout.fragment_profile,container,false);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        fstore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("profileImages");
        star=Holder.findViewById(R.id.replyStarBtn);
        username = Holder.findViewById(R.id.usernameTxt);
        name = Holder.findViewById(R.id.profileName);
        reputationText = Holder.findViewById(R.id.reputationText);
        bio = Holder.findViewById(R.id.bioText);
        editProfile = Holder.findViewById(R.id.editProfile);
        profileImg = Holder.findViewById(R.id.profileImg);
        loader = new ProgressDialog(getActivity());
        userProgressBar=Holder.findViewById(R.id.userProgressBar);
        recyclerView=Holder.findViewById(R.id.userrecview1);
        linearLayoutManager = new LinearLayoutManager(getContext());

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                getActivity().startActivity(intent);
            }
        });


        DocumentReference df = fstore.collection("Users").document(user.getUid());




        GetCurrentUserModelAndSetInfo();
        setUserCourses();

        return Holder;
    }
    private void GetCurrentUserModelAndSetInfo()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Users").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Retrieve the data and map it to UserModel object
                                UserModel userModel = document.toObject(UserModel.class);
                                if (userModel!=null) {
                                    //set username
                                    username.setText("@"+userModel.getUsername());

                                    if (userModel.getProfilePictureUrl() != null) { //set profile pic
                                        String downloadUrl = userModel.getProfilePictureUrl();
                                        Glide.with(ProfileFragment.this).load(downloadUrl).into(profileImg);
                                    }

                                    if (userModel.getName() != null) { //set name
                                        name.setText(userModel.getName());
                                    }
                                    if (userModel.getBio() != null) { //set bio
                                        bio.setText(userModel.getBio());
                                    }
                                    if (userModel.getType() != "Tutor") { //set bio
                                        reputationText.setVisibility(View.GONE);
                                        star.setVisibility(View.GONE);
                                    }else{
                                        reputationText.setText(userModel.getReputation()+"");
                                    }

                                }

                                // ...
                            } else {
                                // Handle the case when the document does not exist
                            }
                        } else {
                            // Handle any errors
                        }
                    }
                });


    }
    private void setUserCourses(){
        PostsDataHolder = new ArrayList<>();
        isLastItemPaged = false;
        Query query = fstore.collection("Users").document(user.getUid()).collection("Formations")
                .orderBy("date", Query.Direction.DESCENDING)
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
                    Toast.makeText(getContext(), "NOT SUCCESSFUL", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void BuildRecyclerView() {

        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new PostAdapter(PostsDataHolder, this);
        recyclerView.setAdapter(adapter);
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

//TODO: add cancel banner
}
