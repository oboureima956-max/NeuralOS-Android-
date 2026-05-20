package com.neuralos;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    UpdateManager updater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = findViewById(R.id.updateBtn);

        updater = new UpdateManager(this);

        updater.checkForUpdate(btn);

        btn.setOnClickListener(v -> updater.startUpdate());
    }
}