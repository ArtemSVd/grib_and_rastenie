package com.example.gribyandrasteniyamap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private  int  orientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_main);
        orientation  = Configuration.ORIENTATION_PORTRAIT;
    }

    private void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        orientation = newConfig.orientation;
        setBackgroundImage(newConfig.orientation);
    }

    private void setBackgroundImage(final int orientation)
    {
        LinearLayout  layout;
        layout = (LinearLayout) findViewById(R.id.mainlayout);

        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            layout.setBackgroundResource(R.drawable.mashrooms_horis);
        else if (orientation == Configuration.ORIENTATION_PORTRAIT)
            layout.setBackgroundResource(R.drawable.mashrooms_vert);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setBackgroundImage(orientation);
    }

}