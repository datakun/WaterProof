package com.kimjunu.waterproof;

public class DatabaseController {

    private static String TAG = "DatabseController";

    public void initDatabase() {
    }

//    @Override
//    protected void finalize() throws Throwable {
//        super.finalize();
//
//        if (mLowerListQuery != null)
//            mLowerListQuery.removeEventListener(mRankListener);
//
//        if (mUpperListQuery != null)
//            mUpperListQuery.removeEventListener(mRankListener);
//    }
//
//    public void createUser(String userId, String name) {
//        User user = new User(name);
//
//        mDatabase.child("users").child(userId).setValue(user);
//    }
//
//    public void updateScore(String userId, long score) {
//        mDatabase.child("users").child(userId).child("score").setValue(score);
//    }
//
//    public void updateArchiveTime(String userId, int level) {
//        mDatabase.child("users").child(userId).child("archiveTime").setValue(level);
//    }
//
//    public void updateArchiveDepth(String userId, int level) {
//        mDatabase.child("users").child(userId).child("archiveDepth").setValue(level);
//    }
//
//    public void updateArchiveScore(String userId, int level) {
//        mDatabase.child("users").child(userId).child("archiveScore").setValue(level);
//    }
//
//    public void deleteUser(String userId) {
//        mDatabase.child("users").child(userId).removeValue();
//    }
//
//    public void addChildEventListener(ChildEventListener listener) {
//        if (mDatabase != null)
//            if (listener != null)
//                mDatabase.addChildEventListener(listener);
//    }
//
//    public void removeEventListener(ChildEventListener listener) {
//        if (mDatabase != null)
//            if (listener != null)
//                mDatabase.removeEventListener(listener);
//    }
//
//    public void removeEventListener(ValueEventListener listener) {
//        if (mDatabase != null)
//            if (listener != null)
//                mDatabase.removeEventListener(listener);
//    }
}
