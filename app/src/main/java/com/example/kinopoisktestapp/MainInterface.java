package com.example.kinopoisktestapp;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface MainInterface {

    @GET("sequeniatesttask/films.json")
    Call<ResponseBody> RESPONSE_BODY_CALL();

}
