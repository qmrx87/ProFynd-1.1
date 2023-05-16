package com.example.profynd.navigation_fragments;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.profynd.R;
import com.example.profynd.adapter.PostAdapter;
import com.example.profynd.interfaces.PostsOnItemClickListner;
import com.example.profynd.models.PostModel;
import com.example.profynd.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nex3z.notificationbadge.NotificationBadge;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class HomeFragment extends Fragment implements PostsOnItemClickListner {
    private SwipeRefreshLayout refresh;
    private ProgressBar progressBar;
    private ArrayList<PostModel> PostsDataHolder;
    private View parentHolder;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    NotificationBadge notificationBadge;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private DocumentReference userInfos, likesRef;
    private CollectionReference postRef;
    private String downloadUrl;

    public String current_location;
    private boolean isLastItemPaged;
    private boolean isScrolling;
    private DocumentSnapshot lastVisible;
    private HashMap<String, Long> tagsMap = new HashMap<String, Long>();




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentHolder = inflater.inflate(R.layout.fragment_home, container, false);
        refresh = parentHolder.findViewById(R.id.homeRefreshLayout);
        progressBar = parentHolder.findViewById(R.id.homeProgressBar);
        recyclerView = parentHolder.findViewById(R.id.recview);
        notificationBadge = parentHolder.findViewById(R.id.badge);

        linearLayoutManager = new LinearLayoutManager(getContext());


        Main();


        return parentHolder ;

    }


    public void Main(){
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        postRef = fstore.collection("Posts");
        userInfos = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());



        userInfos.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                setFeed();
                    if (task.getResult().getLong("unseenNotifications") != null) {
                if (task.getResult().getLong("unseenNotifications").intValue() > 99) {
                    notificationBadge.setText("99+");
                } else {
                    notificationBadge.setNumber(task.getResult().getLong("unseenNotifications").intValue());
                }
                notificationBadge.setNumber(task.getResult().getLong("unseenNotifications").intValue());
            } else {
//                        Toast.makeText(getContext(), "You Don't Have Notifications ! ", Toast.LENGTH_SHORT).show();
            }
        }
    }


        });

        FetchPosts();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onRefresh() {
                refresh.setRefreshing(false);
//                SetFeed();
//                FetchPosts();
            }
        });

    }
    private void setFeed() {

        //adding most demanded posts
        fstore.collection("Posts").orderBy("demandsCount", Query.Direction.DESCENDING).limit(10)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    PostModel post = document.toObject(PostModel.class);
                    userInfos.collection("Feed").document(post.getPostid()).set(post);
                    userInfos.collection("Feed").document(post.getPostid()).update("priority", 1);
                }
            }
        });
        //adding posts  with same location of current User
       DocumentReference uref = fstore.collection("Users").document(user.getUid());
       uref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
           @Override
           public void onSuccess(DocumentSnapshot documentSnapshot) {
               current_location=  documentSnapshot.getString("Location");
               fstore.collection("Posts").whereEqualTo("location",current_location).limit(10)
                       .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                   @Override
                   public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                       for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                           PostModel post = document.toObject(PostModel.class);
                           userInfos.collection("Feed").document(post.getPostid()).set(post);
                           userInfos.collection("Feed").document(post.getPostid()).update("priority", 0);
                       }
                   }
               });
           }
       });

        //adding recommended
        List<Map.Entry<String, Long>> list = new LinkedList<>(tagsMap.entrySet());

        // Sorting the list based on values
        list.sort((o1, o2) -> false ? o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey())
                : o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()) == 0
                ? o2.getKey().compareTo(o1.getKey())
                : o2.getValue().compareTo(o1.getValue()));
        //sorting the map
        tagsMap = list.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));
        ArrayList<String> tags = new ArrayList<>(tagsMap.keySet());

        for (int i = 0; i < 2; i++) {
            try {
                fstore.collection("Posts").whereArrayContains("tags", tags.get(i)).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            PostModel post = document.toObject(PostModel.class);
                            DocumentReference feedRef = userInfos.collection("Feed").document(post.getPostid());
                            feedRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (!documentSnapshot.exists()) {
                                        feedRef.set(post);
                                        feedRef.update("priority", 1);
                                    }
                                    else if(documentSnapshot.getLong("priority").intValue()>1)
                                    {
                                        feedRef.update("priority", 1);
                                    }
                                }
                            });
                        }
                    }
                });
            }catch (IndexOutOfBoundsException e){
                Log.e("Some error has occured:", e.getMessage());
            }
        }


        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -30); //current day -30 days

        //delete extras
        Query query = userInfos.collection("Feed").whereLessThan("Date", c.getTime());
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    doc.getReference().delete();
                }
            }
        });

    }
    private void FetchPosts() {
        PostsDataHolder = new ArrayList<>();
        isLastItemPaged = false;


        Query query = fstore.collection("Users").document(user.getUid()).collection("Feed")
                .orderBy("priority", Query.Direction.ASCENDING)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(20);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        PostModel post = document.toObject(PostModel.class);
                        PostsDataHolder.add(post);

                    }
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(VISIBLE);
                    if (task.getResult().size() > 0)
                        lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                    BuildRecyclerView();

                    //paging
                    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);

                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                isScrolling = true;
                            }
                        }
                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            int firstVisibileItem = linearLayoutManager.findFirstVisibleItemPosition();
                            int visibleItemCount = linearLayoutManager.getChildCount();
                            int totalItemCount = linearLayoutManager.getItemCount();

                            if (isScrolling && (firstVisibileItem + visibleItemCount == totalItemCount) && !isLastItemPaged) {
                                isScrolling = false;

                                Query nextQuery = userInfos.collection("Feed").orderBy("date", Query.Direction.DESCENDING)
                                        .startAfter(lastVisible)
                                        .limit(20);
                                nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            PostModel post = document.toObject(PostModel.class);
                                            PostsDataHolder.add(post);
                                        }
                                        adapter.notifyDataSetChanged();
                                        if (task.getResult().size() < 1) isLastItemPaged = true;
                                        if (task.getResult().size() > 0)
                                            lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                    }
                                });

                            }
                        }
                    };
                    recyclerView.addOnScrollListener(onScrollListener);
                }else{
                    Toast.makeText(getContext(), "NOT SUCCESSFUL", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void BuildRecyclerView() {
        recyclerView = parentHolder.findViewById(R.id.recview);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new PostAdapter(PostsDataHolder, this);
        recyclerView.setAdapter(adapter);
    }

    private void Refresh(ArrayList<String> likes, int position) {
        if (likes != null && position >= 0) {
            adapter.notifyItemChanged(position);
            adapter.notifyDataSetChanged();
            BuildRecyclerView();
        }
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
}