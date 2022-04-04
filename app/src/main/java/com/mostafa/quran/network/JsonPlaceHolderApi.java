package com.mostafa.quran.network;

import com.mostafa.quran.activities.SurahDetailActivity;
import com.mostafa.quran.response.SurahDetailResponse;
import com.mostafa.quran.response.SurahResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface JsonPlaceHolderApi {
    @GET("surah")
    Call<SurahResponse>getSurah();
    @GET("sura/{language}/{id}")
    Call<SurahDetailResponse>getSurahDetail(@Path( "language")String lan,
                                            @Path("id")int surahId);
}
