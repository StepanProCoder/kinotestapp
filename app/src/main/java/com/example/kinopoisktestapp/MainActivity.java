package com.example.kinopoisktestapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    ArrayList<MainData> dataArr;
    MainAdapter adapter;
    RecyclerView recyclerView;
    boolean isSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null) {
            SetMode(savedInstanceState.getBoolean("isSet"), savedInstanceState.getString("text"));
        }
        else
        {
            SetMode(false, "Главная");
        }

        recyclerView = findViewById(R.id.recycler_view);

        GridLayoutManager mLayoutManager = new GridLayoutManager(this,2);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                if(position == 0 || position == adapter.getGenresArr().size()*2+1)
                {
                    return 2;
                }
                else
                {
                    return 1;
                }

            }
        });

        recyclerView.setLayoutManager(mLayoutManager);

        getData();

    }

    private void getData() {

        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://s3-eu-west-1.amazonaws.com/").addConverterFactory(GsonConverterFactory.create(gson)).build();
        MainInterface mainInterface = retrofit.create(MainInterface.class);
        Call<ResponseBody> stringCall = mainInterface.RESPONSE_BODY_CALL();

        stringCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful() && response.body() != null)
                {

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        JSONArray jsonArray = json.getJSONArray("films");
                        dataArr = parseArray(jsonArray);

                        adapter = new MainAdapter(MainActivity.this,dataArr);
                        recyclerView.setAdapter(adapter);


                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("BAD",t.getMessage());
            }
        });
    }

    private ArrayList<MainData> parseArray(JSONArray jsonArray) {

        ArrayList<MainData> newArr = new ArrayList<MainData>();

        for(int i = 0; i < jsonArray.length(); i++)
        {
            try {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                MainData data = SetData(jsonObject);
                newArr.add(data);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return newArr;

    }

    private MainData SetData(JSONObject jsonObject)
    {
        MainData data = new MainData();

        try {   data.setId(jsonObject.getInt("id")); } catch (JSONException e) { e.printStackTrace(); }
        try {   data.setLocalized_name(jsonObject.getString("localized_name")); } catch (JSONException e) { e.printStackTrace(); }
        try {   data.setName(jsonObject.getString("name")); } catch (JSONException e) { e.printStackTrace(); }
        try {   data.setYear(jsonObject.getInt("year")); } catch (JSONException e) { e.printStackTrace(); }
        try {   data.setRating(jsonObject.getDouble("rating")); } catch (JSONException e) { e.printStackTrace(); }
        try {   data.setImage_url(jsonObject.getString("image_url")); } catch (JSONException e) { e.printStackTrace(); }
        try {   data.setDescription(jsonObject.getString("description")); } catch (JSONException e) { e.printStackTrace(); }
        try {
            JSONArray arrJson = jsonObject.getJSONArray("genres");
            String[] arr = new String[arrJson.length()];
            for(int j = 0; j < arrJson.length(); j++)
            {
                arr[j] = arrJson.getString(j);
            }

            data.setGenres(arr);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return data;

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isSet", isSet);
        outState.putString("text", getSupportActionBar().getTitle().toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
        {
            SetMode(false, "Главная");
            return true;
        }
        else
        {
            return false;
        }

    }

    public void SetMode(boolean mode,String text)
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(text);
        actionBar.setDisplayHomeAsUpEnabled(mode);

        if(mode)
        {
            findViewById(R.id.fragment_container_view).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            findViewById(R.id.recycler_view).setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }
        else
        {
            findViewById(R.id.fragment_container_view).setLayoutParams(new LinearLayout.LayoutParams(0,0));
            findViewById(R.id.recycler_view).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        isSet = mode;

    }

}