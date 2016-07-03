package com.kimjunu.waterproof;

import android.os.Bundle;
import android.widget.ListView;

import com.kimjunu.waterproof.model.User;

import java.util.ArrayList;

public class LeaderboardActivity extends BaseActivity {

    private static final String TAG = "Leaderboard";

    private ListView mListView;
    private LeaderboardAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        mAdapter = new LeaderboardAdapter(this);

        mListView = (ListView) findViewById(R.id.lvLeaderboard);

        mListView.setAdapter(mAdapter);

        WaterProofApplication.updateRankList();
        WaterProofApplication.setOnDatabaseEventListener(new WaterProofApplication.DatabaseEventListener() {
            @Override
            public void onUpdatedRankList(ArrayList<User> rankAroundList) {
                mAdapter.clearAll();

                for (User item : rankAroundList) {
                    mAdapter.add(item);
                }

                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
