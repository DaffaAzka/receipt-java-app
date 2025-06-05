package com.example.esemkar_2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esemkar_2.R;
import com.example.esemkar_2.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    List<Category> categories = new ArrayList<>();
    private OnItemClickListener clickListener;

    public interface OnItemClickListener  {
        void onItemClick(Category category);
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.category_name);
            imageView = itemView.findViewById(R.id.category_icon);
        }
    }

    @NonNull
    @Override
    public CategoriesAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_list, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.CategoryViewHolder holder, int position) {
        Category category = categories.get(position);

        holder.textView.setText(category.getName());

//        new ImageLoaderTask(holder.iv).execute(category.getIcon());

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
