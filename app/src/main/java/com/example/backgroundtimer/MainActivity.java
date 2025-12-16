package com.example.backgroundtimer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;


public class MainActivity extends AppCompatActivity {

    TextView textTime;
    Button buttonStart, buttonStop, buttonReset;

    Thread timerThread;
    boolean isRunning = false;

    long startTime = 0;
    long elapsedTime = 0;

    final String CHANNEL_ID = "timer_channel";
    final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textTime = findViewById(R.id.textTime);
        buttonStart = findViewById(R.id.buttonStart);
        buttonStop = findViewById(R.id.buttonStop);
        buttonReset = findViewById(R.id.buttonReset);

        createNotificationChannel();

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        100
                );
            }
        }
    }

    void startTimer() {
        if (isRunning) {
            return;
        }

        isRunning = true;
        startTime = System.currentTimeMillis() - elapsedTime;

        Toast.makeText(this, "Таймер запущен", Toast.LENGTH_SHORT).show();

        timerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    elapsedTime = System.currentTimeMillis() - startTime;

                    long seconds = elapsedTime / 1000;
                    long milliseconds = elapsedTime % 1000;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String timeText = String.format(
                                    "%02d сек %03d мс",
                                    seconds,
                                    milliseconds
                            );

                            textTime.setText(timeText);
                            updateNotification(timeText);
                        }
                    });

                    try {
                        Thread.sleep(10); // обновление ~100 раз в секунду
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        timerThread.start();
    }

    void stopTimer() {
        isRunning = false;
        Toast.makeText(this, "Таймер остановлен", Toast.LENGTH_SHORT).show();
    }

    void resetTimer() {
        isRunning = false;
        elapsedTime = 0;
        textTime.setText("00 сек 000 мс");
        removeNotification();

        Toast.makeText(this, "Таймер сброшен", Toast.LENGTH_SHORT).show();
    }

    void updateNotification(String timeText) {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_media_play)
                        .setContentTitle("Таймер работает")
                        .setContentText("Прошло: " + timeText)
                        .setOngoing(true)
                        .setOnlyAlertOnce(true);

        NotificationManagerCompat.from(this)
                .notify(NOTIFICATION_ID, builder.build());
    }

    void removeNotification() {
        NotificationManagerCompat.from(this)
                .cancel(NOTIFICATION_ID);
    }

    void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Таймер",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
