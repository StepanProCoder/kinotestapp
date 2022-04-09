package com.example.kinopoisktestapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;


public class DescriptionFragment extends Fragment {

    String img;
    String name;
    int year;
    double rating;
    String description;

    public DescriptionFragment() {
        super(R.layout.fragment_description);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        
        if (args != null) {
            img = args.getString("img");
            name = args.getString("name");
            year = args.getInt("year");
            rating = args.getDouble("rating");
            description = args.getString("description");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_description, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView img = view.findViewById(R.id.img);
        TextView name = view.findViewById(R.id.name);
        TextView year = view.findViewById(R.id.year);
        TextView rating = view.findViewById(R.id.rating);
        TextView description = view.findViewById(R.id.description);

        if(this.img == null || this.img == "" || this.img == "null") {
            Glide.with(view).load(getString(R.string.pic_not_found)).diskCacheStrategy(DiskCacheStrategy.ALL).into(img);
        }
        else
        {
            Glide.with(view).load(this.img).diskCacheStrategy(DiskCacheStrategy.ALL).into(img);
        }

        name.setText(this.name);
        year.setText("Год: "+this.year);
        rating.setText("Рейтинг: "+this.rating);
        description.setText(this.description);

    }
}