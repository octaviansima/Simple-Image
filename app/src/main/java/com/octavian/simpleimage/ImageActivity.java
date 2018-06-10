package com.octavian.simpleimage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageActivity extends AppCompatActivity {

    ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imgView = findViewById(R.id.individual_image);
    }

    @Override
    protected void onResume() {
        super.onResume();

        imgView.setImageResource(android.R.color.transparent);

        Intent intent = getIntent();
        String res = intent.getStringExtra("res");
        ImageView img = findViewById(R.id.individual_image);
        Glide.with(this).load(res).into(img);
    }
}
