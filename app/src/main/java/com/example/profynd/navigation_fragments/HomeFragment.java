package com.example.profynd.navigation_fragments;

import static android.view.View.VISIBLE;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import com.example.profynd.R;
import com.example.profynd.adapter.PostAdapter;
import com.example.profynd.interfaces.PostsOnItemClickListner;
import com.example.profynd.models.PostModel;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
               fstore.collection("Posts").whereEqualTo("Location",current_location).limit(10)
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
                }
            }
        });


    }

    private void BuildRecyclerView() {
    }


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