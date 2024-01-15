package com.example.mv_player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class Player_Activity extends AppCompatActivity{
    Button btnPlay, btnNext, btnPrev, btnFor, btnBack;
    TextView txtName, txtStart, txtStop;
    SeekBar seekMusic;
    ImageView imageView;

    String sName;
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;
    Thread updateSeekbar;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnPrev = findViewById(R.id.previousButton);
        btnNext = findViewById(R.id.nextButton);
        btnPlay = findViewById(R.id.playButton);
        btnFor = findViewById(R.id.forwardButton);
        btnBack = findViewById(R.id.backwardButton);
        txtName = findViewById(R.id.txtsc);
        txtStart = findViewById(R.id.txtStart);
        txtStop = findViewById(R.id.txtStop);
        seekMusic = findViewById(R.id.seekbar);
        imageView = findViewById(R.id.imageView);

        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        position = bundle.getInt("pos",0);
        txtName.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        sName = mySongs.get(position).getName();
        txtName.setText(sName);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();

        updateSeekbar = new Thread()
        {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentposition = 0;

                while (currentposition<totalDuration)
                {
                    try {
                        sleep(500);
                        currentposition = mediaPlayer.getCurrentPosition();
                        seekMusic.setProgress(currentposition);
                    }
                    catch (InterruptedException | IllegalStateException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
        seekMusic.setMax(mediaPlayer.getDuration());
        updateSeekbar.start();
        seekMusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.color_primary), PorterDuff.Mode.MULTIPLY);
        seekMusic.getThumb().setColorFilter(getResources().getColor(R.color.color_primary), PorterDuff.Mode.SRC_IN);

        seekMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });

        String endTime = createTime(mediaPlayer.getDuration());
        txtStop.setText(endTime);

        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTime(mediaPlayer.getCurrentPosition());
                txtStart.setText(currentTime);
                handler.postDelayed(this, delay);
            }
        }, delay);



        btnPlay.setOnClickListener(view -> {
            if (mediaPlayer.isPlaying())
            {
                btnPlay.setBackgroundResource(R.drawable.ic_play);
                mediaPlayer.pause();
            }
            else
            {
                btnPlay.setBackgroundResource(R.drawable.ic_pause);
                mediaPlayer.start();
            }
        });
        mediaPlayer.setOnCompletionListener(mediaPlayer -> btnNext.performClick());


        btnNext.setOnClickListener(view -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            position = ((position+1) % mySongs.size());
            Uri u = Uri.parse(mySongs.get(position).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
            sName = mySongs.get(position).getName();
            txtName.setText(sName);
            mediaPlayer.start();
            btnPlay.setBackgroundResource(R.drawable.ic_pause);
            startAnimation(imageView);
        });

        btnPrev.setOnClickListener(view -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            position = ((position-1)<0)?(mySongs.size()-1):(position-1);

            Uri u = Uri.parse(mySongs.get(position).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
            sName = mySongs.get(position).getName();
            txtName.setText(sName);
            mediaPlayer.start();
            btnPlay.setBackgroundResource(R.drawable.ic_pause);
            animatorBackward(imageView);
        });

        btnFor.setOnClickListener(view -> {
            if (mediaPlayer.isPlaying())
            {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
            }
        });



        btnBack.setOnClickListener(view -> {
            if (mediaPlayer.isPlaying())
            {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
            }
        });
    }

    public void startAnimation(View view)
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f,360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }
    public void animatorBackward(View view)
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f,-360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    public String createTime(int duration)
    {
        String time = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;

        time+=min+":";

        if (sec<10)
        {
            time+="0";
        }
        time+=sec;

        return  time;
    }
}