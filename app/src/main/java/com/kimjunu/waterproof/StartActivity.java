package com.kimjunu.waterproof;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

public class StartActivity extends BaseActivity {

    boolean mIsBackPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (WaterProofApplication.mAuthUser == null)
            signIn();
    }

    // 액티비티 정지 시, 리스너 제거
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (WaterProofApplication.mAuthListener != null) {
            WaterProofApplication.mAuth.removeAuthStateListener(WaterProofApplication.mAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        if (mIsBackPressed) {
            signOut();

            super.onBackPressed();

            return;
        }

        this.mIsBackPressed = true;
        Toast.makeText(this, R.string.close_message, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mIsBackPressed = false;
            }
        }, 2000);
    }

    public void openPlayActivity(View view) {
        Intent intent = new Intent(this, PlayActivity.class);
        startActivity(intent);
    }

    public void openSettingsActivity(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void openLeaderboardActivity(View view) {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        startActivity(intent);
    }
}
