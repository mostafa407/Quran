package com.mostafa.quran.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.mostafa.quran.repository.SurahDetailRepo;
import com.mostafa.quran.response.SurahDetailResponse;

public class SurahDetailViewModel extends ViewModel {
    public SurahDetailRepo surahDetailRepo;

    public SurahDetailViewModel() {
        surahDetailRepo=new SurahDetailRepo();
    }
    public LiveData<SurahDetailResponse>getSurahDetail(String lan, int id){
        return surahDetailRepo.getSurahDetail(lan, id);
    }
}
