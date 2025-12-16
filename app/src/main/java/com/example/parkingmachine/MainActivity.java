package com.example.parkingmachine;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView tvState;
    Button btnReserve, btnStart, btnFinish, btnPay, btnReset;

    ParkingStateMachine parkingMachine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvState = findViewById(R.id.tvState);
        btnReserve = findViewById(R.id.btnReserve);
        btnStart = findViewById(R.id.btnStart);
        btnFinish = findViewById(R.id.btnFinish);
        btnPay = findViewById(R.id.btnPay);
        btnReset = findViewById(R.id.btnReset);

        parkingMachine = new ParkingStateMachine();
        updateStateText();

        btnReserve.setOnClickListener(v ->
                handleAction(parkingMachine.handleEvent(ParkingEvent.RESERVE)));

        btnStart.setOnClickListener(v ->
                handleAction(parkingMachine.handleEvent(ParkingEvent.START_SESSION)));

        btnFinish.setOnClickListener(v ->
                handleAction(parkingMachine.handleEvent(ParkingEvent.FINISH_SESSION)));

        btnPay.setOnClickListener(v ->
                handleAction(parkingMachine.handleEvent(ParkingEvent.PAY)));

        btnReset.setOnClickListener(v ->
                handleAction(parkingMachine.handleEvent(ParkingEvent.RESET)));
    }

    private void handleAction(boolean success) {
        if (success) {
            updateStateText();
        } else {
            Toast.makeText(this,
                    "Недопустимый переход состояния",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void updateStateText() {
        tvState.setText("Состояние: " + parkingMachine.getCurrentState());
    }
}
