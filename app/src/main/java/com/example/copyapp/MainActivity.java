package com.example.copyapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private FrameLayout sliderContainer;
    private View sliderTrack;
    private View sliderThumb;
    private TextView sliderText;
    private float maxX;
    private float startX;
    private float endX;
    private TextView tvMemory;
    private TextView tvUpload;
    private TextView tvDownload;
    private final android.os.Handler handler = new android.os.Handler();
    private Runnable statsRunnable;



    private int thumbMargin;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sliderContainer = findViewById(R.id.sliderContainer);
        sliderTrack = findViewById(R.id.sliderTrack);
        sliderThumb = findViewById(R.id.sliderThumb);
        sliderText = findViewById(R.id.sliderText);
        tvMemory = findViewById(R.id.tvMemory);
        tvUpload = findViewById(R.id.tvUpload);
        tvDownload = findViewById(R.id.tvDownload);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_config) {
                startActivity(new Intent(MainActivity.this, ConfigActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return true;
        });


        statsRunnable = new Runnable() {
            @Override
            public void run() {
                updateStatsRandom();
                handler.postDelayed(this, 1000);
            }
        };

        sliderContainer.post(() -> {
            FrameLayout.LayoutParams lp =
                    (FrameLayout.LayoutParams) sliderThumb.getLayoutParams();

            startX = lp.leftMargin;
            endX = sliderContainer.getWidth()
                    - sliderThumb.getWidth()
                    - lp.rightMargin;
        });

        sliderThumb.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {

                case MotionEvent.ACTION_MOVE:

                    float newX = event.getRawX()
                            - sliderContainer.getX()
                            - sliderThumb.getWidth() / 2f;

                    newX = Math.max(startX, Math.min(newX, endX));
                    sliderThumb.setX(newX);

                    float progress1 = (newX - startX) / (endX - startX);

                    if (progress1 > 0.5f) {
                        sliderTrack.setBackgroundResource(R.drawable.bg_slider_green);
                    } else {
                        sliderTrack.setBackgroundResource(R.drawable.bg_slider_black);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    float progress2 =
                            (sliderThumb.getX() - startX) / (endX - startX);

                    if (progress2 >= 0.9f) {
                        onSliderSuccess();
                    } else {
                        resetSlider();
                    }
                    break;
            }
            return true;
        });
    }

    private void onSliderSuccess() {

        sliderThumb.animate()
                .x(endX)
                .setDuration(150)
                .start();

        sliderTrack.setBackgroundResource(R.drawable.bg_slider_green);
        sliderText.setText("Подключено");
        handler.post(statsRunnable);
    }

    private void resetSlider() {
        sliderThumb.animate()
                .x(startX)
                .setDuration(150)
                .start();

        sliderTrack.setBackgroundResource(R.drawable.bg_slider_black);
        sliderText.setText("Сдвинь для подключения");
        handler.removeCallbacks(statsRunnable);

        tvMemory.setText("0" + " KB");
        tvUpload.setText("0" + " KB");
        tvDownload.setText("0" + " KB");
    }

    private void updateStatsRandom() {
        int memory = randomBetween(50, 1024);      // KB
        int upload = randomBetween(1, 500);         // KB
        int download = randomBetween(1, 800);       // KB

        tvMemory.setText(memory + " KB");
        tvUpload.setText(upload + " KB");
        tvDownload.setText(download + " KB");
    }

    private int randomBetween(int min, int max) {
        return min + (int) (Math.random() * (max - min + 1));
    }

}
