package com.example.profynd.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.profynd.R;
import com.example.profynd.models.PostModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.myviewholder> {

    private ArrayList<PostModel> PostsHolder;
    private Context context;
    private PostsOnItemClickListner mListner;



    public PostAdapter(ArrayList<PostModel> postsHolder ,PostsOnItemClickListner mlistner) {
        PostsHolder = postsHolder;
        mListner = mlistner;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post, parent, false);
        return new myviewholder(view, mListner, PostsHolder);
    }


    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        Glide.with(context).load(PostsHolder.get(position).getFormation_img()).into(holder.img);
        holder.Title.setText(PostsHolder.get(position).getTitle());
        holder.Username.setText("@"+PostsHolder.get(position).getUsername());
        holder.Location.setText(PostsHolder.get(position).getLocation());
        if (PostsHolder.get(position).getPrice() != 0) {
            holder.Price.setText(PostsHolder.get(position).getPrice()+"DA");
        }else{
            holder.Price.setText("Free");
        }
    }





    @Override
    public int getItemCount() {
        return PostsHolder.size();
    }

    public static class myviewholder extends RecyclerView.ViewHolder {


        ImageView img;
        TextView Title,Username,Price,Location;

        private FirebaseAuth auth;
        private FirebaseUser user;
        private FirebaseFirestore fstore;

        public myviewholder (@NonNull View itemView , PostsOnItemClickListner listner , ArrayList<PostModel> postHolder){
            super(itemView);
            img = itemView.findViewById(R.id.img);
            Title = itemView.findViewById(R.id.formation_title);
            Username = itemView.findViewById(R.id.usernamep);
            Price = itemView.findViewById(R.id.price);
            Location = itemView.findViewById(R.id.formation_localisation);

            auth = FirebaseAuth.getInstance();
            fstore = FirebaseFirestore.getInstance();
            user = auth.getCurrentUser();







            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null) {
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            PostModel postModel = postHolder.get(position);
                            listner.onItemClick(position, postModel);
                        }
                    }
                }
            });



            itemView.findViewById(R.id.questionMenuBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAbsoluteAdapterPosition();
                    PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
                    if (user.getUid().equals(postHolder.get(position).getPublisher()))
                        popupMenu.inflate(R.menu.my_post_menu);
                    else popupMenu.inflate(R.menu.post_menu);


                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            boolean b = false;
                            PostModel postModel = postHolder.get(position);
                            if (menuItem.getTitle().equals("Delete")) {
                                postHolder.remove(position);
                               // adapter.notifyItemRemoved(position);
                                DeleteFromFeed(postModel);
                                Toast.makeText(view.getContext(), "Delete", Toast.LENGTH_SHORT).show();
                                b = true;
                            }
                            else {
                                DocumentReference postRef = FirebaseFirestore.getInstance().collection("Posts").document(postModel.getPostid());
                                postRef.update("reportsCount", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(view.getContext(), "Your report has been sent", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                b = true;
                            }
                            return b;
                        }
                    });
                    popupMenu.show();
                }
            });
        }

        private void DeleteFromFeed(PostModel postModel) {
            Query postRef = FirebaseFirestore.getInstance().collectionGroup("Feed").whereEqualTo("postid", postModel.getPostid());
            postRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                        doc.getReference().delete();
                    }
                }
            });
        }


    }
    public interface PostsOnItemClickListner {
        void onItemClick(int position, PostModel postModel);
    }
}
