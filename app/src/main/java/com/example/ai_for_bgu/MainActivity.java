package com.example.ai_for_bgu;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText etAge, etHeight, etWeight;
    RadioGroup rgGender;
    Spinner spGoal;
    Button btnCalculate;
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etAge = findViewById(R.id.etAge);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        rgGender = findViewById(R.id.rgGender);
        spGoal = findViewById(R.id.spGoal);
        btnCalculate = findViewById(R.id.btnCalculate);
        tvResult = findViewById(R.id.tvResult);

        // Цели питания
        String[] goals = {"Похудение", "Поддержание", "Набор массы"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                goals
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGoal.setAdapter(adapter);

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Проверка пустых полей
                if (etAge.getText().toString().isEmpty() ||
                        etHeight.getText().toString().isEmpty() ||
                        etWeight.getText().toString().isEmpty()) {

                    Toast.makeText(MainActivity.this,
                            "Пожалуйста, заполните все поля",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                int age = Integer.parseInt(etAge.getText().toString());
                int height = Integer.parseInt(etHeight.getText().toString());
                double weight = Double.parseDouble(etWeight.getText().toString());

                // Проверка корректности значений
                if (age <= 0) {
                    Toast.makeText(MainActivity.this,
                            "Возраст должен быть больше 0",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (height < 50) {
                    Toast.makeText(MainActivity.this,
                            "Рост должен быть больше 50 см",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (weight < 20) {
                    Toast.makeText(MainActivity.this,
                            "Вес должен быть больше 20 кг",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Проверка выбора пола
                if (rgGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(MainActivity.this,
                            "Выберите пол",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioButton rbGender = findViewById(rgGender.getCheckedRadioButtonId());
                boolean isMale = rbGender.getText().toString().equals("Мужчина");

                String goal = spGoal.getSelectedItem().toString();

                double calories = BguCalc.calculateCalories(
                        isMale,
                        age,
                        height,
                        weight,
                        goal
                );

                String result = BguCalc.calculateBJU(calories, weight);
                tvResult.setText(result);
            }
        });
    }
}
