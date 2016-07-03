package com.kimjunu.waterproof;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PlayActivity extends BaseActivity {

    private static final String TAG = "Play";

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        tvResult = (TextView) findViewById(R.id.tvResult);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void onCreateData(View view) {
    }

    public void onReadData(View view) {
    }

    public void onUpdateData(View view) {
    }

    public void onDeletedata(View view) {
    }
}
