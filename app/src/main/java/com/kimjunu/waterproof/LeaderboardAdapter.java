package com.kimjunu.waterproof;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kimjunu.waterproof.model.User;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class LeaderboardAdapter extends BaseAdapter {
    private final Context context;

    private ArrayList<User> mList;

    public LeaderboardAdapter(Context context) {
        this.mList = new ArrayList<>();
        this.context = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();

        TextView tvName;
        TextView tvScore;
        final ImageView ivProfile;
        LeaderboardHolder holder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.leaderboard_item, viewGroup, false);

            tvName = (TextView) view.findViewById(R.id.tvLeaderboardName);
            tvScore = (TextView) view.findViewById(R.id.tvLeaderboardScore);
            ivProfile = (ImageView) view.findViewById(R.id.ivProfile);

            holder = new LeaderboardHolder();
            holder.tvName = tvName;
            holder.tvScore = tvScore;
            holder.ivProfile = ivProfile;

            view.setTag(holder);
        } else {
            holder = (LeaderboardHolder) view.getTag();
            tvName = holder.tvName;
            tvScore = holder.tvScore;
            ivProfile = holder.ivProfile;
        }

        tvName.setText(mList.get(i).username);

        String strScore = Long.toString(mList.get(i).score)
                + " " + context.getResources().getString(R.string.score_postfix);
        tvScore.setText(strScore);

        new RetrievePhotoTask(ivProfile).execute(mList.get(i).photoURL);

        return view;
    }

    public void add(User user) {
        mList.add(user);
    }

    public void remove(int i) {
        mList.remove(i);
    }

    public void clearAll() {
        mList.clear();
    }

    public void sortDesc() {
        Collections.sort(mList, new WaterProofApplication.ScoreDescCompare());
    }

    private class LeaderboardHolder {
        TextView tvName;
        TextView tvScore;
        ImageView ivProfile;
    }

    class RetrievePhotoTask extends AsyncTask<String, Void, Bitmap> {

        public ImageView ivProfile;

        public RetrievePhotoTask(ImageView iv) {
            ivProfile = iv;

//            Drawable d = ivProfile.getDrawable();
//            if (d instanceof BitmapDrawable) {
//                Bitmap b = ((BitmapDrawable) d).getBitmap();
//                if (!b.isRecycled()) {
//                    b.recycle();
//                }
//            }
        }

        protected Bitmap doInBackground(String... photoURL) {
            try {
                Bitmap bmpProfile;

                HttpURLConnection connection = (HttpURLConnection) new URL(photoURL[0]).openConnection();
                connection.connect();

                InputStream input = connection.getInputStream();

                bmpProfile = BitmapFactory.decodeStream(input);

                return bmpProfile;
            } catch (Exception e) {

                return null;
            }
        }

        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap == null) {
                Drawable drawable = context.getResources()
                        .getDrawable(android.R.drawable.sym_def_app_icon, context.getTheme());

                ivProfile.setImageDrawable(drawable);
            } else {
                ivProfile.setImageBitmap(bitmap);
            }
        }
    }
}
