package com.example.profynd;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AddPostActivity extends AppCompatActivity {
    Button publish ;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        publish=findViewById(R.id.postBtn);
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(AddPostActivity.this);
                dialog.setContentView(R.layout.addformation_dialog);
                dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

            }
        });

    }
}