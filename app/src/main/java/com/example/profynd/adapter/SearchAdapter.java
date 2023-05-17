package com.example.profynd.adapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.profynd.R;
import com.example.profynd.UserProfileActivity;
import com.example.profynd.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.util.ArrayList;



public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.UsersViewHolder> {

    Context context;
    ArrayList<UserModel> userModelArrayList;
    private final OnItemClickListener mOnItemClickListener;

    public SearchAdapter(Context context, ArrayList<UserModel> userModelArrayList, OnItemClickListener mOnItemClickListener) {
        this.context = context;
        this.userModelArrayList = userModelArrayList;
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cardview_search_user,parent,false);
        return new UsersViewHolder(v,mOnItemClickListener, userModelArrayList);

    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {

        holder.Name.setText(userModelArrayList.get(position).getName());
        holder.Username.setText("@"+userModelArrayList.get(position).getUsername());
        Glide.with(this.context).load(userModelArrayList.get(position).getProfilePictureUrl()).into(holder.profilePictureUrl);

    }

    @Override
    public int getItemCount() {
        return userModelArrayList.size();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        TextView Name;
        TextView Username;
        ImageView profilePictureUrl;
        OnItemClickListener onItemClickListener;

        public UsersViewHolder(View itemView,OnItemClickListener onItemClickListener, ArrayList<UserModel> userModels) {
            super(itemView);
            Name = itemView.findViewById(R.id.usertext);
            Username =  itemView.findViewById(R.id.usernametext);
            profilePictureUrl =  itemView.findViewById(R.id.userimage);
            this.onItemClickListener = onItemClickListener;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(getAbsoluteAdapterPosition());
                    UserModel user = userModels.get(getAbsoluteAdapterPosition());
                    if (user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    {
                        //go to user fragment
                        Toast.makeText(view.getContext().getApplicationContext(), "opening profile", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Intent intent = new Intent(view.getContext(), UserProfileActivity.class);
                        intent.putExtra("Tag", (Serializable) user);
                        view.getContext().startActivity(intent);
                    }
                }
            });

        }

    }

    public interface OnItemClickListener{
        void onItemClick(int position);

    }
}

