package com.example.copyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ConfigActivity extends AppCompatActivity {
    private View selectedCard = null;
    private TextView selectedProtocol = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_configs);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        bottomNav.setSelectedItemId(R.id.nav_config);

        LinearLayout configFolder = findViewById(R.id.configFolder);
        LinearLayout configList = findViewById(R.id.configListContainer);
        ImageView arrow = findViewById(R.id.arrowIcon);

        FrameLayout config1 = findViewById(R.id.config1);
        LinearLayout card1 = findViewById(R.id.card1);
        TextView protocol1 = findViewById(R.id.protocol1);

        FrameLayout config2 = findViewById(R.id.config2);
        LinearLayout card2 = findViewById(R.id.card2);
        TextView protocol2 = findViewById(R.id.protocol2);

        FrameLayout config3 = findViewById(R.id.config3);
        LinearLayout card3 = findViewById(R.id.card3);
        TextView protocol3 = findViewById(R.id.protocol3);

        config1.setOnClickListener(v -> selectConfig(card1, protocol1));
        config2.setOnClickListener(v -> selectConfig(card2, protocol2));
        config3.setOnClickListener(v -> selectConfig(card3, protocol3));

        final boolean[] isOpen = {true};

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(ConfigActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return true;
        });

        configFolder.setOnClickListener(v -> {
            if (isOpen[0]) {
                configList.setVisibility(View.GONE);
                arrow.animate().rotation(0f).setDuration(150).start();
            } else {
                configList.setVisibility(View.VISIBLE);
                arrow.animate().rotation(180f).setDuration(150).start();
            }
            isOpen[0] = !isOpen[0];
        });

        config1.setOnClickListener(v -> selectConfig(card1, protocol1));
        config2.setOnClickListener(v -> selectConfig(card2, protocol2));
        config3.setOnClickListener(v -> selectConfig(card3, protocol3));

        selectConfig(card1, protocol1);
    }

    private void resetConfigs() {
        findViewById(R.id.config1)
                .setBackgroundResource(R.drawable.bg_config_card);

        findViewById(R.id.config2)
                .setBackgroundResource(R.drawable.bg_config_card);

        findViewById(R.id.config3)
                .setBackgroundResource(R.drawable.bg_config_card);
    }

    private void selectConfig(View card, TextView protocol) {

        // 1. Сброс предыдущего
        if (selectedCard != null && selectedProtocol != null) {
            selectedProtocol.setVisibility(View.GONE);

            ViewGroup.MarginLayoutParams lp =
                    (ViewGroup.MarginLayoutParams) selectedCard.getLayoutParams();
            lp.leftMargin = 0;
            selectedCard.setLayoutParams(lp);

            selectedCard.setBackgroundResource(R.drawable.bg_config_card);
        }

        // 2. Применение к новому
        protocol.setVisibility(View.VISIBLE);

        ViewGroup.MarginLayoutParams lp =
                (ViewGroup.MarginLayoutParams) card.getLayoutParams();
        lp.leftMargin = dpToPx(72);
        card.setLayoutParams(lp);

        card.setBackgroundResource(R.drawable.bg_config_card_selected);

        // 3. Запоминаем выбранный
        selectedCard = card;
        selectedProtocol = protocol;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
