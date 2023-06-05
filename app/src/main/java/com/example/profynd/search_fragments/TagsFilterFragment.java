package com.example.profynd.search_fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.profynd.BottomNavigationActivity;
import com.example.profynd.R;
import com.example.profynd.adapter.SearchRecommendationAdapter;
import com.example.profynd.interfaces.SearchOnItemClick;
import com.example.profynd.models.PostModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class TagsFilterFragment extends Fragment implements SearchOnItemClick {

    private View parentHolder;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ArrayList<PostModel> FormationsDataHolder;
    private SearchRecommendationAdapter adapter;
    private FirebaseFirestore fstore;
    private CollectionReference postRef;
    private ImageButton backBtn;
    private Chip primary_school, middle_school, high_school,
            programming,english,security,networking,uiux,french,spain,
            mathematics,physics,science,languages ,other;
    private SwipeRefreshLayout refresh;
    private LinearLayout emptyTagSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentHolder = inflater.inflate(R.layout.fragment_tags_filter, container, false);

        fstore = FirebaseFirestore.getInstance();
        postRef = fstore.collection("Posts");
        FormationsDataHolder = new ArrayList<>();
        refresh = parentHolder.findViewById(R.id.searchRefresh);
        backBtn = parentHolder.findViewById(R.id.tagsFilterBackBtn);
        progressBar = parentHolder.findViewById(R.id.tagsProgressBar);
        recyclerView = parentHolder.findViewById(R.id.searchTagsRecview);
        emptyTagSearch = parentHolder.findViewById(R.id.emptyTagSearch);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), BottomNavigationActivity.class);
                startActivity(i);
                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });

        InitChips();
        progressBar.setVisibility(View.VISIBLE);
        FetchAllFormations();
        ShowTagFilterResults();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ShowTagFilterResults();
                refresh.setRefreshing(false);
            }
        });
        return parentHolder;
    }

    //***Display questions on recyclerview***//
    private void BuildRecyclerView(){
        recyclerView = parentHolder.findViewById(R.id.searchTagsRecview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SearchRecommendationAdapter(FormationsDataHolder,this);
        recyclerView.setAdapter(adapter);
    }

    //***Fetch All Formations***//
    private void FetchAllFormations(){
        postRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    FormationsDataHolder = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        PostModel post = document.toObject(PostModel.class);

                    }

                    BuildRecyclerView();
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                }else{
                    Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //***Filter the Formations according to the tag selected and the number of demands***//
    private void ShowTagFilterResults() {
        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    if (compoundButton.getText().toString().equals("All")){
                        emptyTagSearch.setVisibility(View.GONE);
                        FetchAllFormations();
                    }else {
                        postRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    FormationsDataHolder = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        PostModel post = document.toObject(PostModel.class);
                                        ArrayList<String> tags = post.getTags();
                                        if (tags!=null){
                                            for (String tag : tags){
                                                if (compoundButton.getText().toString().equals(tag)){
                                                    FormationsDataHolder.add(post);
                                                }
                                            }
                                        }
                                    }
                                    if (FormationsDataHolder.size() != 0){
                                        emptyTagSearch.setVisibility(View.GONE);

                                        BuildRecyclerView();
                                        progressBar.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                    }else {
                                        BuildRecyclerView();
                                        progressBar.setVisibility(View.GONE);
                                        emptyTagSearch.setVisibility(View.VISIBLE);
                                    }

                                } else {
                                    Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
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

    //***Initialize chips***//
    private void InitChips() {

        primary_school =  parentHolder.findViewById(R.id.Primary_Schools);
        middle_school =  parentHolder.findViewById(R.id.Middle_Schools);
        high_school =  parentHolder.findViewById(R.id.High_Schools);
        programming =  parentHolder.findViewById(R.id.Programmings);
        english =  parentHolder.findViewById(R.id.Englishs);
        security =  parentHolder.findViewById(R.id.Securitys);
        networking =  parentHolder.findViewById(R.id.Networkings);
        uiux =  parentHolder.findViewById(R.id.ui_uxs);
        french =  parentHolder.findViewById(R.id.Frenshs);
        spain =  parentHolder.findViewById(R.id.Spains);
        mathematics =  parentHolder.findViewById(R.id.Mathematicss);
        physics =  parentHolder.findViewById(R.id.Physicss);
        science =  parentHolder.findViewById(R.id.Sciences);
        other =  parentHolder.findViewById(R.id.others);
        languages =  parentHolder.findViewById(R.id.Languagess);

    }

    @Override
    public void onItemClick(int position) {

    }
}