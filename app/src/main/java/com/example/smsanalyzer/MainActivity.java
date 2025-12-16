package com.example.smsanalyzer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    Button buttonAnalyze;
    TextView textIncome, textExpense;
    ListView listViewSms;

    ArrayList<String> smsList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAnalyze = findViewById(R.id.buttonAnalyze);
        textIncome = findViewById(R.id.textIncome);
        textExpense = findViewById(R.id.textExpense);
        listViewSms = findViewById(R.id.listViewSms);

        smsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                smsList
        );
        listViewSms.setAdapter(adapter);

        requestSmsPermission();

        buttonAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readSmsInBackground();
            }
        });
    }

    void requestSmsPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_SMS},
                    200
            );
        }
    }

    void readSmsInBackground() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
        ) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "Нет разрешения на чтение SMS", Toast.LENGTH_SHORT).show();
            return;
        }

        Thread smsThread = new Thread(new Runnable() {
            @Override
            public void run() {

                smsList.clear();

                double totalIncome = 0;
                double totalExpense = 0;

                Uri uri = Uri.parse("content://sms/inbox");
                Cursor cursor = getContentResolver().query(
                        uri,
                        null,
                        null,
                        null,
                        "date DESC"
                );

                if (cursor != null) {
                    int count = 0;

                    while (cursor.moveToNext() && count < 30) {

                        String body = cursor.getString(
                                cursor.getColumnIndexOrThrow("body")
                        );

                        String lowerText = body.toLowerCase();

                        boolean isIncome = false;
                        boolean isExpense = false;

                        if (lowerText.contains("зачис")
                                || lowerText.contains("пополн")
                                || lowerText.contains("поступ")
                                || lowerText.contains("+")) {
                            isIncome = true;
                        }

                        if (lowerText.contains("списан")
                                || lowerText.contains("оплат")
                                || lowerText.contains("покуп")
                                || lowerText.contains("-")) {
                            isExpense = true;
                        }

                        double amount = extractAmount(body);

                        if (isIncome && amount > 0) {
                            totalIncome += amount;
                            smsList.add("ЗАЧИСЛЕНИЕ: " + amount + "\n" + body);
                        } else if (isExpense && amount > 0) {
                            totalExpense += amount;
                            smsList.add("СПИСАНИЕ: " + amount + "\n" + body);
                        } else {
                            smsList.add("ДРУГОЕ\n" + body);
                        }

                        count++;
                    }

                    cursor.close();
                }

                double finalIncome = totalIncome;
                double finalExpense = totalExpense;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textIncome.setText("Сумма зачислений: " + finalIncome);
                        textExpense.setText("Сумма списаний: " + finalExpense);
                        adapter.notifyDataSetChanged();

                        Toast.makeText(
                                MainActivity.this,
                                "Анализ завершён",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            }
        });

        smsThread.start();
    }

    double extractAmount(String text) {

        Pattern pattern = Pattern.compile("(\\d+[.,]?\\d*)");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            try {
                String value = matcher.group(1).replace(",", ".");
                return Double.parseDouble(value);
            } catch (Exception e) {
                return 0;
            }
        }

        return 0;
    }

}
