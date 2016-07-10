package com.kimjunu.waterproof;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kimjunu.waterproof.model.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class WaterProofApplication extends Application {

    private static final String TAG = "WaterProofApplication";

    public static final int RC_SIGN_IN = 9001;

    private static final int AROUND_COUNT = 10;

    public static FirebaseAuth mAuth;
    public static FirebaseAuth.AuthStateListener mAuthListener;
    public static FirebaseUser mAuthUser;

    public static GoogleApiClient mGoogleApiClient;

    private static ArrayList<User> mRankAroundUserList;
    private static User mUser;

    private static DatabaseReference mDatabase;

    private static ChildEventListener mRankListener;

    private static Query mUpperListQuery;
    private static Query mLowerListQuery;

    private static int mUpdateFlag = 0;

    public interface DatabaseEventListener {
        //        void onUpdatedRankList(ArrayList<User> rankAroundList);
        void onAddedAroundUser(User user);
    }

    public static void setOnDatabaseEventListener(DatabaseEventListener listener) {
        mDatabaseEventListener = listener;
    }

    public static void removeEventListener(DatabaseEventListener listener) {
        mDatabaseEventListener = null;
    }

    public interface UserEventListener {
        void onUpdatedUser(User user);
    }

    public static void setOnUserEventListener(UserEventListener listener) {
        mUserEventListener = listener;
    }

    public static void removeEventListener(UserEventListener listener) {
        mUserEventListener = null;
    }

    private static DatabaseEventListener mDatabaseEventListener;
    private static UserEventListener mUserEventListener;

    @Override
    public void onCreate() {
        super.onCreate();

        mRankAroundUserList = new ArrayList<>();

        initFirebaseAuth();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        if (mLowerListQuery != null)
            mLowerListQuery.removeEventListener(mRankListener);

        if (mUpperListQuery != null)
            mUpperListQuery.removeEventListener(mRankListener);
    }

    private void initFirebaseAuth() {
        // mAuth 가져오기
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mRankListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);

                int flag = 0;
                for (User item : mRankAroundUserList) {
                    if (Objects.equals(item.uid, user.uid))
                        break;

                    flag++;
                }

                if (flag != mRankAroundUserList.size())
                    return;

                mRankAroundUserList.add(user);

                if (mDatabaseEventListener != null)
                    mDatabaseEventListener.onAddedAroundUser(user);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        // 인증 결과 리스너 생성
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mAuthUser = firebaseAuth.getCurrentUser();

                if (mAuthUser != null) {
                    // 로그인
                    updateUserInfo();
                } else {
                    // 로그아웃
                    mUser = null;
                }
            }
        };

        mAuth.addAuthStateListener(mAuthListener);
    }

    public static User getUser() {
        return mUser;
    }

    public static ArrayList<User> getRankAroundUserList() {
        return mRankAroundUserList;
    }

    public void updateUserInfo() {
        mDatabase.child("users").child(mAuthUser.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mUser = dataSnapshot.getValue(User.class);

                        if (mUser == null) {
                            Uri photoUri = mAuthUser.getPhotoUrl();

                            if (photoUri == null)
                                mUser = new User(mAuthUser.getUid(), mAuthUser.getDisplayName(), "");
                            else
                                mUser = new User(mAuthUser.getUid(), mAuthUser.getDisplayName(), photoUri.toString());

                            mDatabase.child("users").child(mAuthUser.getUid()).setValue(mUser);

//                            Bitmap bmp =  BitmapFactory.decodeResource(getResources(), R.drawable.common_full_open_on_phone);//your image
//                            ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
//                            bmp.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
//                            bmp.recycle();
//                            byte[] byteArray = bYtE.toByteArray();
//                            String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public static void updateRankList() {
        mUpdateFlag = 0;

        mRankAroundUserList.clear();

        mDatabase.child("users").child(mAuthUser.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mUser = dataSnapshot.getValue(User.class);

                        updateUpperListQuery(mUser.score, AROUND_COUNT + 1);
                        updateLowerListQuery(mUser.score, AROUND_COUNT + 1);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private static void updateUpperListQuery(long score, final int size) {
        mUpperListQuery = mDatabase.child("users").orderByChild("score")
                .endAt(score).limitToLast(size);

        if (mUpperListQuery != null) {
            mUpperListQuery.removeEventListener(mRankListener);
            mUpperListQuery.addChildEventListener(mRankListener);
        }
    }

    private static void updateLowerListQuery(long score, final int size) {
        mLowerListQuery = mDatabase.child("users").orderByChild("score")
                .startAt(score).limitToFirst(size);

        if (mLowerListQuery != null) {
            mLowerListQuery.removeEventListener(mRankListener);
            mLowerListQuery.addChildEventListener(mRankListener);
        }
    }

    static class ScoreDescCompare implements Comparator<User> {

        @Override
        public int compare(User user1, User user2) {
            return user1.score > user2.score ? -1 : user1.score > user2.score ? 1 : 0;
        }
    }

    public static void findCurrentUser() {
        mDatabase.child("users").child(mAuthUser.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mUser = dataSnapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public static void updateScore(long score) {
        if (mAuthUser != null)
            mDatabase.child("users").child(mAuthUser.getUid()).child("score").setValue(score);
    }

    public static void updateArchiveTime(int level) {
        if (mAuthUser != null)
            mDatabase.child("users").child(mAuthUser.getUid()).child("archiveTime").setValue(level);
    }

    public static void updateArchiveDepth(int level) {
        if (mAuthUser != null)
            mDatabase.child("users").child(mAuthUser.getUid()).child("archiveDepth").setValue(level);
    }

    public static void updateArchiveScore(int level) {
        if (mAuthUser != null)
            mDatabase.child("users").child(mAuthUser.getUid()).child("archiveScore").setValue(level);
    }

    public static void revokeUser() {
        if (mAuthUser != null)
            mDatabase.child("users").child(mAuthUser.getUid()).removeValue();
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }
}
