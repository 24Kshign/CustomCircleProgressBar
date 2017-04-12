package com.share.jack.customcircleprogressbar;

import android.app.Activity;
import android.os.Bundle;

import com.share.jack.customcircleprogressbar.widget.CustomCircleProgressBar;

public class MainActivity extends Activity {

    private CustomCircleProgressBar progressBarOne;
    private CustomCircleProgressBar progressBarTwo;
    private CustomCircleProgressBar progressBarThree;
    private CustomCircleProgressBar progressBarFour;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBarOne = (CustomCircleProgressBar) findViewById(R.id.am_progressbar_one);
        progressBarTwo = (CustomCircleProgressBar) findViewById(R.id.am_progressbar_two);
        progressBarThree = (CustomCircleProgressBar) findViewById(R.id.am_progressbar_three);
        progressBarFour = (CustomCircleProgressBar) findViewById(R.id.am_progressbar_four);

        progressBarOne.setProgress(20);
        progressBarTwo.setProgress(40);
        progressBarThree.setProgress(60);
        progressBarFour.setProgress(80);

    }
}