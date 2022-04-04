package com.mostafa.quran.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mostafa.quran.network.Api;
import com.mostafa.quran.network.JsonPlaceHolderApi;
import com.mostafa.quran.response.SurahResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SurahRepo {
    private JsonPlaceHolderApi jsonPlaceHolderApi;

    public SurahRepo() {
        jsonPlaceHolderApi= Api.getRetrofit().create(JsonPlaceHolderApi.class);

    }
    public LiveData<SurahResponse>getSurah(){
        MutableLiveData<SurahResponse> data=new MutableLiveData<>();
        jsonPlaceHolderApi.getSurah().enqueue(new Callback<SurahResponse>() {
            @Override
            public void onResponse(Call<SurahResponse> call, Response<SurahResponse> response) {
               data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<SurahResponse> call, Throwable t) {
                data.setValue(null);

            }
        });
        return data;
    }
}
