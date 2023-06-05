package com.example.profynd;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.profynd.search_fragments.SearchUserFragment;
import com.example.profynd.search_fragments.TagsFilterFragment;


public class SearchActivity extends AppCompatActivity{

    ImageButton sbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        BeginTagSearch();

    }

    private void BeginTagSearch(){
        sbtn = findViewById(R.id.container_searchBtn);
        sbtn.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().replace(R.id.searchContainer,
                new TagsFilterFragment()).commit();

        sbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.searchContainer,
                        new SearchUserFragment()).commit();
                if(sbtn.getVisibility()==View.VISIBLE) {
                    sbtn.setVisibility(View.INVISIBLE);
                }else{
                sbtn.setVisibility(View.VISIBLE);
            }}
        });
    }
}