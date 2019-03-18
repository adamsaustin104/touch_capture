package com.junipersys.a3_chambertest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    public InterceptTouchFrameLayout itfl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itfl = new InterceptTouchFrameLayout(getApplicationContext());

    }
}
