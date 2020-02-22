package com.youseforex.support_and_restincance;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ActivityAboutUs extends AppCompatActivity {

    private TextView text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us_layout);

        text = findViewById(R.id.text);

        text.setText(getResources().getString(getIntent().getIntExtra("text" , 0)));

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
    }
}
