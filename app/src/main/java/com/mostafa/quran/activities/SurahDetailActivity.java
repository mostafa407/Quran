package com.mostafa.quran.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mostafa.quran.R;
import com.mostafa.quran.adapter.SurahDetailAdapter;
import com.mostafa.quran.common.Common;
import com.mostafa.quran.model.SurahDetail;
import com.mostafa.quran.response.SurahDetailResponse;
import com.mostafa.quran.viewmodel.SurahDetailViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SurahDetailActivity extends AppCompatActivity {

    private TextView surahName, surahType, surahTranslation;
    private int no;
    private RecyclerView recyclerView;
    private List<SurahDetail>list;
    private SurahDetailAdapter adapter;
    private SurahDetailViewModel surahDetailViewModel;
    private String urdu="urdu_junagarhi";
    private String hindi="hindi_omari";
    private String english="english_hilali_khan";
    private EditText searchView;
    private ImageButton settingButton;
    private RadioGroup radioGroup, audioGroup;
    private RadioButton translateradioButton, qariSelectButton;
    private String lan;
    private String qariAB="abdul_basit_murattal";
    private String qariAW="muhammad_siddeeq_al-minshaawee";
    private String qr;
    Handler handler=new Handler();
    SeekBar seekBar;
    TextView startTime, totalTime;
    ImageButton playButton;
    MediaPlayer mediaPlayer;
    private  String str;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surah_detail);
        init();

        no=getIntent().getIntExtra(Common.SURAH_NO, 0);
        surahName.setText(getIntent().getStringExtra(Common.SURAH_NAME));
        surahType.setText(getIntent().getStringExtra(Common.SURAH_TYPE)+
                ""+getIntent().getIntExtra(Common.SURAH_TOTAL_AYA,0)+"AYA");
        surahTranslation.setText(getIntent().getStringExtra(Common.SURAH_TRANSLATION));
        recyclerView.setHasFixedSize(true);
        list= new ArrayList<>();
        try {
            ListenAudio(qariAB);
        } catch (IOException e) {
            e.printStackTrace();
        }
        surahTranslation(english,no);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                filter(s.toString());
            }
        });
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(SurahDetailActivity.this,
                        R.style.BottomSheetDialogTheme);
                LayoutInflater inflater= LayoutInflater.from(getApplicationContext());
                View view=inflater.inflate(R.layout.bottom_sheet_layout,
                        findViewById(R.id.sheetContainer));
                view.findViewById(R.id.save_setting).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        radioGroup=view.findViewById(R.id.translate_group);
                        audioGroup=view.findViewById(R.id.audio_group);
                        int selected=radioGroup.getCheckedRadioButtonId();
                        translateradioButton=view.findViewById(selected);
                        if (selected==-1){
                            Toast.makeText(SurahDetailActivity.this, "nothing selected", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(SurahDetailActivity.this, "selected", Toast.LENGTH_SHORT).show();
                        }
                        if (translateradioButton.getText().toString().toLowerCase().trim().equals("urdu")){
                          lan=urdu;
                        }else if (translateradioButton.getText().toString().toLowerCase().trim().equals("hindi")) {
                        lan=hindi;
                        }else if(translateradioButton.getText().toString().toLowerCase().trim().equals("english ")){
                            lan=english;
                        }
                        surahTranslation(lan, no);

                        int id=audioGroup.getCheckedRadioButtonId();
                        qariSelectButton=view.findViewById(id);
                        if (qariSelectButton.getText().toString().trim().toLowerCase().equals("abdul basit murattal")){

                            qr=qariAB;
                        }else if (qariSelectButton.getText().toString().trim().toLowerCase().equals("muhammad_siddeeq_al-minshaawee")){

                            qr=qariAW;
                        }
                        mediaPlayer.reset();
                        mediaPlayer.release();
                        try {
                            ListenAudio(qr);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        bottomSheetDialog.dismiss();
                    }

                });
                bottomSheetDialog.setContentView(view);
                bottomSheetDialog.show();
            }
        });
    }



    private void init(){
        surahName=findViewById(R.id.surah_name);
        surahType=findViewById(R.id.type);
        surahTranslation=findViewById(R.id.translation);
        recyclerView=findViewById(R.id.surah_detail_rv);
        searchView=findViewById(R.id.search_view);
        settingButton=findViewById(R.id.setting_button);
    }

    public void surahTranslation(String lan, int id) {

        if (list.size()>0){
            list.clear();
        }
        surahDetailViewModel=new ViewModelProvider(this).get(SurahDetailViewModel.class);
        surahDetailViewModel.getSurahDetail(lan, id).observe(this,
                surahDetailResponse -> {
            for (int i=0; i< surahDetailResponse.getList().size();i++){
                list.add(new SurahDetail
                                     (surahDetailResponse.getList().get(i).getId(),
                                        surahDetailResponse.getList().get(i).getSura(),
                                        surahDetailResponse.getList().get(i).getAya(),
                                        String.valueOf(surahDetailResponse.getList().get(i).getArabic_text()),
                                        String.valueOf(surahDetailResponse.getList().get(i).getTranslation()),
                                        String.valueOf(surahDetailResponse.getList().get(i).getFootnotes())));
            }
            if (list.size()!=0){
                adapter=new SurahDetailAdapter(this, list);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    private void filter(String id) {

        ArrayList<SurahDetail>arrayList=new ArrayList<>();
        for (SurahDetail detail: list){
            if (String.valueOf(detail.getId()).contains(id)){
                arrayList.add(detail);
            }
        }
        adapter.filter(arrayList);
    }
    @SuppressLint("ClickableViewAccessibility")
    private void ListenAudio(String qari) throws IOException {
        playButton=findViewById(R.id.play_button);
        startTime=findViewById(R.id.start_time);
        totalTime=findViewById(R.id.total_time);
        seekBar=findViewById(R.id.seek_bar);
        mediaPlayer=new MediaPlayer();
       seekBar.setMax(100);
       playButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (mediaPlayer.isPlaying()){
                   handler.removeCallbacks(updater);
                   mediaPlayer.pause();
                   playButton.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
               }else {
                   mediaPlayer.start();
                   playButton.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
                   updateSeeker();
               }
           }
       });
       preparedMediaPlayer(qari);
       seekBar.setOnTouchListener(new View.OnTouchListener() {
           @SuppressLint("ClickableViewAccessibility")
           @Override
           public boolean onTouch(View v, MotionEvent event) {
            SeekBar seekBar=(SeekBar) v;
            int playPosition=(mediaPlayer.getDuration()/100)*seekBar.getProgress();
            mediaPlayer.seekTo(playPosition);
            startTime.setText(timeToMillisecond(mediaPlayer.getCurrentPosition()));
               return false;
           }
       });
       mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            seekBar.setSecondaryProgress(percent);
        }

    });
       mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            seekBar.setProgress(0);
            playButton.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
            startTime.setText("0:00");
            totalTime.setText("0:00");
            mediaPlayer.reset();
            try {
                preparedMediaPlayer(qari);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });
    }

    private void preparedMediaPlayer(String qari) throws IOException {

        if (no< 10){
            str="00"+no;
        }else if (no<100){
            str="0"+no;
        }else if (no>=100){
         str=String.valueOf(no);
        }
        mediaPlayer.setDataSource("https://download.quranicaudio.com/quran/"+qari+"/"+str.trim()+".mp3");
        mediaPlayer.prepare();
        totalTime.setText(timeToMillisecond(mediaPlayer.getDuration()));
    }

    private Runnable updater=new Runnable() {
        @Override
        public void run() {
            updateSeeker();
            long currentDuration=mediaPlayer.getCurrentPosition();
            startTime.setText(timeToMillisecond(currentDuration));
        }

    };
    private void updateSeeker(){
        if (mediaPlayer.isPlaying())
        {
            seekBar.setProgress((int)(((float)mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration())*100));
             handler.postDelayed(updater,1000);
        }
    }
    private String timeToMillisecond(long millisecond){
        String timeString="";
        String secondString="";
        int hours=(int)(millisecond/(1000*60*60));
        int minutes=(int) (millisecond %(1000*60*60))/(1000*60);
        int second=(int) ((millisecond %(1000*60*60))%(1000*60)/1000);

        if (hours>0){
            timeString=hours+":";
        }if (second<10){
            secondString="0"+second;
        }else {
            secondString=""+second;
        }
        timeString=timeString+minutes+":"+secondString;
        return timeString;
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer.isPlaying()) {
            handler.removeCallbacks(updater);
            mediaPlayer.pause();
            playButton.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        if (mediaPlayer.isPlaying()) {
            handler.removeCallbacks(updater);
            mediaPlayer.pause();
            playButton.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        if (mediaPlayer.isPlaying()) {
            handler.removeCallbacks(updater);
            mediaPlayer.pause();
            playButton.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
        }
        super.onPause();
    }
}