package com.example.currencyrates;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Button buttonLoad;
    TextView textUsd, textEur;
    Spinner spinnerFirst, spinnerSecond;
    ArrayList<String> currencyList;
    ArrayAdapter<String> spinnerAdapter;
    JSONObject valuteData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonLoad = findViewById(R.id.buttonLoad);
        textUsd = findViewById(R.id.textUsd);
        textEur = findViewById(R.id.textEur);

        spinnerFirst = findViewById(R.id.spinnerFirst);
        spinnerSecond = findViewById(R.id.spinnerSecond);
        currencyList = new ArrayList<>();

        buttonLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCurrencyRates();
            }
        });

        spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                currencyList
        );
        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerFirst.setAdapter(spinnerAdapter);
        spinnerSecond.setAdapter(spinnerAdapter);

        spinnerFirst.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateRates();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        spinnerSecond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateRates();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    void loadCurrencyRates() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                String jsonString = "";

                try {
                    URL url = new URL("https://www.cbr-xml-daily.ru/daily_json.js");
                    HttpURLConnection connection =
                            (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream())
                    );

                    String line;
                    StringBuilder builder = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }

                    reader.close();
                    jsonString = builder.toString();

                } catch (Exception e) {
                    jsonString = "";
                }

                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONObject valute = jsonObject.getJSONObject("Valute");

                    valuteData = valute;

                    currencyList.clear();

                    Iterator<String> keys = valute.keys();
                    while (keys.hasNext()) {
                        currencyList.add(keys.next());
                    }
                    String firstCurrency = "USD";
                    String secondCurrency = "EUR";

                    double firstValue = valute
                            .getJSONObject(firstCurrency)
                            .getDouble("Value");

                    double secondValue = valute
                            .getJSONObject(secondCurrency)
                            .getDouble("Value");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            spinnerAdapter.notifyDataSetChanged();

                            textUsd.setText(firstCurrency + ": " + firstValue + " ₽");
                            textEur.setText(secondCurrency + ": " + secondValue + " ₽");

                            Toast.makeText(
                                    MainActivity.this,
                                    "Курсы валют загружены",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });

                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(
                                    MainActivity.this,
                                    "Ошибка обработки данных",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
                }

            }
        });

        thread.start();
    }

    void updateRates() {

        if (valuteData == null) {
            return;
        }

        try {
            String firstCurrency = spinnerFirst.getSelectedItem().toString();
            String secondCurrency = spinnerSecond.getSelectedItem().toString();

            double firstValue = valuteData
                    .getJSONObject(firstCurrency)
                    .getDouble("Value");

            double secondValue = valuteData
                    .getJSONObject(secondCurrency)
                    .getDouble("Value");

            textUsd.setText(firstCurrency + ": " + firstValue + " ₽");
            textEur.setText(secondCurrency + ": " + secondValue + " ₽");

        } catch (Exception e) {
            Toast.makeText(this, "Ошибка обновления курса", Toast.LENGTH_SHORT).show();
        }
    }

}
