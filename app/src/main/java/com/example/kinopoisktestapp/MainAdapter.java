package com.example.kinopoisktestapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int GENRE = 0;
    private static final int FILM = 1;
    private static final int EMPTY = 2;
    private int mainIndex = -1;

    private ArrayList<MainData> dataArr;
    private ArrayList<MainData> dataOrig;
    private ArrayList<String> genresArr;
    private MainActivity activity;

    public MainAdapter(MainActivity activity, ArrayList<MainData> dataArr)
    {

        Comparator<MainData> comparator = new Comparator<MainData>() {
            @Override
            public int compare(MainData mainData, MainData t1) {

                return mainData.getLocalized_name().compareTo(t1.getLocalized_name());

            }
        };

        Collections.sort(dataArr,comparator);

        this.activity = activity;
        this.dataArr = dataArr;
        dataOrig = new ArrayList<MainData>(dataArr);

        genresArr = GenGenresArr(dataArr);

    }

    public ArrayList<String> getGenresArr() {
        return genresArr;
    }

    private ArrayList<String> GenGenresArr(ArrayList<MainData> data)
    {
        ArrayList<String> genres = new ArrayList<String>();

        for(int i = 0; i < data.size(); i++)
        {
            String[] arr = data.get(i).getGenres();

            if(arr == null)
            {
                continue;
            }

            for(int k = 0; k < arr.length; k++)
            {
                boolean flag = true;

                for(int j = 0; j < genres.size(); j++)
                {
                    if(genres.get(j).equals(arr[k]))
                    {
                        flag = false;
                        break;
                    }
                }

                if(flag)
                {
                    genres.add(arr[k]);
                }

            }
        }

        return genres;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == FILM) {
            Log.d("TYPE","FILM");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_main, parent, false);
            return new FilmHolder(view);
        }
        else if(viewType == GENRE)
        {
            Log.d("TYPE","GENRE");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_elem, parent, false);
            return new GenreHolder(view);
        }
        else
        {
            Log.d("TYPE","EMPTY");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_elem, parent, false);
            return new GenreHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if(getItemViewType(position) == FILM) {

            MainData data = dataArr.get(position - 2 - genresArr.size()*2);

            if(data.getImage_url() == null || data.getImage_url() == "" || data.getImage_url() == "null") {
                Glide.with(activity).load(activity.getString(R.string.pic_not_found)).diskCacheStrategy(DiskCacheStrategy.ALL).into(((FilmHolder) holder).imageView);
            }
            else
            {
                Glide.with(activity).load(data.getImage_url()).diskCacheStrategy(DiskCacheStrategy.ALL).into(((FilmHolder) holder).imageView);
            }

            ((FilmHolder)holder).textView.setText(data.getLocalized_name());

            ((FilmHolder) holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Bundle args = new Bundle();
                    args.putString("img", data.getImage_url());
                    args.putString("name", data.getName());
                    args.putInt("year", data.getYear());
                    args.putDouble("rating", data.getRating());
                    args.putString("description", data.getDescription());

                    Fragment descriptionFragment = new DescriptionFragment();
                    descriptionFragment.setArguments(args);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, descriptionFragment).commit();
                    activity.SetMode(true, data.getLocalized_name());

                }
            });

        }
        else if(getItemViewType(position) == GENRE)
        {
            String val = "";

            if(position == 0)
            {
                val = "Жанры";
            }
            else if(position == genresArr.size()*2+1)
            {
                val = "Фильмы";
            }
            else
            {
                val = genresArr.get(position/2);
            }

            ((GenreHolder)holder).textView.setText(val);

            ((GenreHolder)holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String genreText = (String) new GenreHolder(view).textView.getText();

                    if(genreText.equals("Жанры") || genreText.equals("Фильмы"))
                    {
                        return;
                    }

                    mainIndex = position;
                    notifyDataSetChanged();

                    //removing items
                    int s = dataArr.size();
                    dataArr.clear();

                    for(int i = 0; i < s; i++)
                    {
                        notifyItemRemoved(2 + genresArr.size()*2);
                    }



                    //adding essential items
                    for(int i = 0; i < dataOrig.size(); i++)
                    {
                        boolean flag = false;
                        String[] arr = dataOrig.get(i).getGenres();

                        if(arr == null)
                        {
                            continue;
                        }


                        for(int j = 0; j < arr.length; j++)
                        {

                            if(arr[j].equals(genreText))
                            {
                                flag = true;
                                break;
                            }
                        }

                        if(flag)
                        {
                            Log.d("ADD","ADDED");
                            dataArr.add(dataOrig.get(i));
                            notifyItemInserted(2 + genresArr.size()*2 + (dataArr.size() - 1));
                        }

                    }


                }
            });

            if(position == mainIndex)
            {
                ((CardView) ((GenreHolder)holder).view).setCardBackgroundColor(Color.parseColor("#add8e6"));
            }
            else
            {
                ((CardView) ((GenreHolder)holder).view).setCardBackgroundColor(Color.parseColor("#ffffff"));
            }


        }

    }

    @Override
    public int getItemCount() {
        return 2 + genresArr.size()*2 + dataArr.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (position < genresArr.size()*2 + 2) {

            if(position == 0 || position%2 == 1)
            {
                return GENRE;
            }
            else
            {
                return EMPTY;
            }

        }
        else {
            return FILM;
        }

    }

    public class FilmHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;
        View view;

        public FilmHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            imageView = itemView.findViewById(R.id.image_view);
            textView = itemView.findViewById(R.id.text_view);
        }
    }

    public class GenreHolder extends RecyclerView.ViewHolder {

        TextView textView;
        View view;

        public GenreHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            textView = itemView.findViewById(R.id.text_view);
        }
    }

}
