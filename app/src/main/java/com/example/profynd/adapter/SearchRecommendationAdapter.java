package com.example.profynd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.profynd.R;
import com.example.profynd.interfaces.SearchOnItemClick;
import com.example.profynd.models.PostModel;

import java.util.ArrayList;


public class SearchRecommendationAdapter extends RecyclerView.Adapter<SearchRecommendationAdapter.ViewHolder1> {
    private ArrayList<PostModel> FormationsHolder;
    private Context context;
    private SearchOnItemClick mListner;

    public SearchRecommendationAdapter(ArrayList<PostModel> formationsHolder, SearchOnItemClick mListner) {
        FormationsHolder = formationsHolder;
        this.mListner = mListner;
    }

    @NonNull
    @Override
    public ViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post,parent,false);
        return new ViewHolder1(view , mListner);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder1 holder, int position) {
        Glide.with(context).load(FormationsHolder.get(position).getFormation_img()).into(holder.img);
        holder.Title.setText(FormationsHolder.get(position).getTitle());
        holder.Username.setText("@"+ FormationsHolder.get(position).getUsername());
        holder.Location.setText(FormationsHolder.get(position).getLocation());
        holder.Price.setText(Integer.toString(FormationsHolder.get(position).getPrice()));

    }

    @Override
    public int getItemCount() {
        return FormationsHolder.size();
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder {

        ImageView AnswerBtn;
        ImageView img;
        TextView Title,Username,Location, Price;
        public ViewHolder1(@NonNull View itemView,SearchOnItemClick listner) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            Title = itemView.findViewById(R.id.formation_title);
            Username = itemView.findViewById(R.id.usernamep);
            Location = itemView.findViewById(R.id.formation_localisation);
            Price = itemView.findViewById(R.id.price);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null){
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listner.onItemClick(position);
                        }
                    }
                }
            });

        }
    }
}
