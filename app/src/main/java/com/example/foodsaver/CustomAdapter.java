package com.example.foodsaver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    Context context;
    List<String> recipeNamesList;
    List<String> recipeUrlList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView rowTitle;
        AppCompatImageView rowImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowTitle = itemView.findViewById(R.id.recipe_title);
            rowImage = itemView.findViewById(R.id.recipe_image_view);
        }
    }

    public CustomAdapter(Context context, List<String> recipeNamesList, List<String> recipeUrlList) {
        this.context = context;
        this.recipeNamesList = recipeNamesList;
        this.recipeUrlList = recipeUrlList;
    }

    @NonNull
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.single_recipe, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position) {
        holder.rowTitle.setText(recipeNamesList.get(position));
        Glide.with(holder.itemView.getContext()).load(recipeUrlList.get(position)).into(holder.rowImage);
    }

    @Override
    public int getItemCount() {
        return recipeNamesList.size();
    }
}
