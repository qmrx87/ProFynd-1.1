package com.example.profynd;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddPostActivity extends AppCompatActivity {
    Button publish ;
    Dialog dialog;
    private EditText titleEditTxt, bodyEditTxt,priceEditTxt;
    private ImageButton returnButton;
    TextInputEditText available_places;
    private Button postButton,okay;
    private CircleImageView profileImg;
    RadioGroup radioGroup ;
    RadioButton collective , individual ;
    int places;
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
        dialog = new Dialog(AddPostActivity.this);

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = titleEditTxt.getText().toString().trim();
                String priceString = priceEditTxt.getText().toString().trim();
                String body = bodyEditTxt.getText().toString().trim();

                if (TextUtils.isEmpty(title)) {
                    titleEditTxt.setError("Title is required!");
                    titleEditTxt.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(priceString)) {
                    priceEditTxt.setError("Price is required!");
                    priceEditTxt.requestFocus();
                    return;
                } else if (!TextUtils.isDigitsOnly(priceString)) {
                    priceEditTxt.setError("Price must be a number!");
                    priceEditTxt.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(body)) {
                    bodyEditTxt.setError("Body is required!");
                    bodyEditTxt.requestFocus();
                    return;
                }

                int price = Integer.parseInt(priceString);
                dialog.setContentView(R.layout.addformation_dialog);
                dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                    okay = dialog.findViewById(R.id.okay);
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
                        RadioButton individual = dialog.findViewById(R.id.individual);
                        RadioButton collective = dialog.findViewById(R.id.collective);

                        if (radioGroup.getCheckedRadioButtonId() == -1) {
                            // No radio button is checked, show a toast
                            Toast.makeText(AddPostActivity.this, "Please select a formation type", Toast.LENGTH_SHORT).show();
                        } else if (individual.isChecked()) {
                            // If the individual radio button is selected, disable the available_places EditText
                            available_places.setEnabled(false);
                            dialog.hide();
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
                                dialog.hide();
                            }
                        }
                    }
                });



            }


        });

    }
}