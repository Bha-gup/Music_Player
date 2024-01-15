package com.example.mv_player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.Objects;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Objects.requireNonNull(getSupportActionBar()).hide();

        final Intent i = new Intent(MainActivity2.this,MainActivity.class);
        new Handler().postDelayed(() -> {
            startActivity(i);
            finish();
        },1000);
    }
}