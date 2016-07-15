package com.kimjunu.waterproof;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class PlayActivity extends BaseActivity {

    private static final String TAG = "Play";

    private View.OnTouchListener mViewTouchListener = null;
    private View.OnClickListener mViewClickListener = null;

    private Player mPlayer = null;

    private ImageView ivAccelerate = null;
    private ImageView ivMoveLeft = null;
    private ImageView ivMoveRight = null;
    private ImageView ivLifePoint = null;
    private ImageView ivPlayer = null;
    private ArrayList<ImageView> ivBackgroundList = null;
    private ImageView ivCurrentBg = null;
    private ImageView ivLeftBg = null;
    private ImageView ivRightBg = null;
    private ImageView ivLowerBg = null;
    private ImageView ivLowerLeftBg = null;
    private ImageView ivLowerRightBg = null;
    private LinearLayout llMoverContainer = null;
    private LinearLayout llLifeContainer = null;
    private TextView tvLifePoint = null;

    private Handler mStatusChecker = null;
    private Runnable mStatusMaker = null;

    private boolean isPlayStarted = false;
    private boolean isAccelerating = false;
    private boolean isTurnLeft = false;
    private boolean isTurnRight = false;
    boolean mIsBackPressed = false;

    private int screenWidth = 0;
    private int screenHeight = 0;
    private float walkPoint = 0;
    private int imageWidth = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        ivAccelerate = (ImageView) findViewById(R.id.ivAccelerate);
        ivMoveLeft = (ImageView) findViewById(R.id.ivMoveLeft);
        ivMoveRight = (ImageView) findViewById(R.id.ivMoveRight);
        ivLifePoint = (ImageView) findViewById(R.id.ivLifePoint);
        ivPlayer = (ImageView) findViewById(R.id.ivPlayer);
        llMoverContainer = (LinearLayout) findViewById(R.id.llMoverContainer);
        llLifeContainer = (LinearLayout) findViewById(R.id.llLifeContainer);
        tvLifePoint = (TextView) findViewById(R.id.tvLifePoint);

        ivBackgroundList = new ArrayList<>();
        ivBackgroundList.add((ImageView) findViewById(R.id.ivBg1));
        ivBackgroundList.add((ImageView) findViewById(R.id.ivBg2));
        ivBackgroundList.add((ImageView) findViewById(R.id.ivBg3));
        ivBackgroundList.add((ImageView) findViewById(R.id.ivBg4));
        ivBackgroundList.add((ImageView) findViewById(R.id.ivBg5));
        ivBackgroundList.add((ImageView) findViewById(R.id.ivBg6));

        llMoverContainer.setVisibility(View.INVISIBLE);
        llLifeContainer.setVisibility(View.INVISIBLE);

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        imageWidth = Math.max(screenWidth, screenHeight) + 100;

        for (ImageView item : ivBackgroundList) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) item.getLayoutParams();
            params.width = imageWidth;
            params.height = imageWidth;

            item.setLayoutParams(params);
        }

        ivCurrentBg = ivBackgroundList.get(1);
        ivLeftBg = ivBackgroundList.get(0);
        ivRightBg = ivBackgroundList.get(2);
        ivLowerBg = ivBackgroundList.get(4);
        ivLowerLeftBg = ivBackgroundList.get(3);
        ivLowerRightBg = ivBackgroundList.get(5);

        walkPoint = (float) (screenHeight / 1000.0);

        mViewTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (view.getId()) {
                    case R.id.ivAccelerate:
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                            isAccelerating = true;

                            ivAccelerate.setColorFilter(getColor(android.R.color.black));
                        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            isAccelerating = false;

                            ivAccelerate.setColorFilter(getColor(android.R.color.white));
                        }

                        break;
                    case R.id.llMoverContainer:
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN
                                || motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                            if (motionEvent.getX() < ivMoveLeft.getRight()) {
                                isTurnLeft = true;
                                isTurnRight = false;

                                ivMoveLeft.setColorFilter(getColor(android.R.color.black));
                                ivMoveRight.setColorFilter(getColor(android.R.color.white));
                            } else if (motionEvent.getX() > ivMoveRight.getLeft()) {
                                isTurnLeft = false;
                                isTurnRight = true;

                                ivMoveLeft.setColorFilter(getColor(android.R.color.white));
                                ivMoveRight.setColorFilter(getColor(android.R.color.black));
                            }
                        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            isTurnLeft = false;
                            isTurnRight = false;

                            ivMoveLeft.setColorFilter(getColor(android.R.color.white));
                            ivMoveRight.setColorFilter(getColor(android.R.color.white));
                        }

                        break;
                }

                return true;
            }
        };

        mViewClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.ivAccelerate:
                        isPlayStarted = true;

                        ivAccelerate.setOnTouchListener(mViewTouchListener);
                        llMoverContainer.setOnTouchListener(mViewTouchListener);

                        ivAccelerate.setOnClickListener(null);

                        mStatusChecker = new Handler();
                        mStatusChecker.postDelayed(mStatusMaker, 100);

                        llMoverContainer.setVisibility(View.VISIBLE);
                        llLifeContainer.setVisibility(View.VISIBLE);

                        break;
                }
            }
        };

        ivAccelerate.setOnClickListener(mViewClickListener);

        mPlayer = new Player();

        mStatusMaker = new Runnable() {
            @Override
            public void run() {
                // 방향 전환
                if (isTurnLeft)
                    mPlayer.turnLeft();
                else if (isTurnRight)
                    mPlayer.turnRight();

                // 가속
                if (!isAccelerating)
                    mPlayer.deaccelerate();
                else {
                    mPlayer.accelerate();

//                    if (!isTurnRight && !isTurnLeft) {
                    if (mPlayer.getAngle() < 0 && !isTurnRight)
                        mPlayer.turnLeft();
                    else if (mPlayer.getAngle() > 0 && !isTurnLeft)
                        mPlayer.turnRight();
//                    }
                }

                // 라이프 소비
                mPlayer.consumeLife();

                // 방향 전환 애니메이션
                ivPlayer.animate()
                        .rotation(mPlayer.getAngle())
                        .setDuration(50)
                        .start();

                // 전진 애니메이션
                float movingPoint = mPlayer.getCurrentSpeed() * walkPoint;
                float turnPoint = mPlayer.getAngle() / 80 * movingPoint;
                movingPoint = movingPoint - (mPlayer.getAngle() / 10 * walkPoint);
                if (movingPoint < 0)
                    movingPoint = 0;

                if (mPlayer.getCurrentSpeed() > 0) {
                    if (screenHeight / 5 > ivPlayer.getY()) {
                        ivPlayer.animate()
                                .translationYBy(movingPoint)
                                .setDuration(80)
                                .start();
                    } else {
                        ivPlayer.setY(screenHeight / 5);
                    }
                } else {
                    if (screenHeight / 7 < ivPlayer.getY()) {
                        ivPlayer.animate()
                                .translationYBy(-walkPoint * 3)
                                .setDuration(80)
                                .start();
                    } else {
                        ivPlayer.setY(screenHeight / 7);
                    }
                }

                int index;
                for (index = 0; index < ivBackgroundList.size(); index++) {
                    ImageView item = ivBackgroundList.get(index);

                    Rect currentRect = new Rect();
                    currentRect.set((int) item.getX(),
                            (int) item.getY(),
                            (int) item.getX() + item.getWidth(),
                            (int) item.getY() + item.getHeight());

                    if (currentRect.contains((int) ivPlayer.getX(), (int) ivPlayer.getY())
                            && item.getY() <= 0)
                        break;
                }

                determineBackgroundView(index);

                ivCurrentBg.setY(ivCurrentBg.getY() - movingPoint);

                ivLeftBg.setY(ivCurrentBg.getY());
                ivRightBg.setY(ivCurrentBg.getY());

                ivLeftBg.setX(ivCurrentBg.getX() - imageWidth + 5);
                ivRightBg.setX(ivCurrentBg.getX() + imageWidth * 2 - 5);

                ivLowerLeftBg.setY(ivLeftBg.getY() + imageWidth - 5);
                ivLowerBg.setY(ivCurrentBg.getY() + imageWidth - 5);
                ivLowerRightBg.setY(ivRightBg.getY() + imageWidth - 5);

                ivLowerLeftBg.setX(ivCurrentBg.getX() - imageWidth + 5);
                ivLowerBg.setX(ivCurrentBg.getX());
                ivLowerRightBg.setX(ivCurrentBg.getX() + imageWidth * 2 - 5);

//                Log.d(TAG, (screenHeight - ivBackgroundList.get(1).getHeight() + movingPoint) + "");

//                if (ivBackgroundList.get(1).getY() > screenHeight - ivBackgroundList.get(1).getHeight() + movingPoint
//                        && movingPoint >= 0) {
//                    ivBackgroundList.get(1).animate()
//                            .translationYBy(-movingPoint)
//                            .setDuration(80)
//                            .start();
//                } else {
//                    ivBackgroundList.get(1).setY(0);
//                }
//
//                if (ivBackgroundList.get(1).getX() > screenWidth - ivBackgroundList.get(1).getWidth() - turnPoint
//                        && ivBackgroundList.get(1).getX() < -turnPoint) {
//                    ivBackgroundList.get(1).animate()
//                            .translationXBy(turnPoint)
//                            .setDuration(80)
//                            .start();
//                } else {
//                    int x = ivBackgroundList.get(1).getWidth() / 2 - screenWidth / 2;
//                    ivBackgroundList.get(1).setX(-x);
//                }

                // 라이프 소비 애니메이션
                tvLifePoint.setText(String.valueOf(mPlayer.getLifePoint()));

                float percent = (mPlayer.getLifePoint() / (float) 1000.0) * 0xff;

                Drawable drawable = ivLifePoint.getDrawable();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    VectorDrawable vectorDrawable = (VectorDrawable) drawable;
                    assert vectorDrawable != null;
                    vectorDrawable.setAlpha((int) percent);

                    ivLifePoint.setImageDrawable(vectorDrawable);
                }

                mStatusChecker.postDelayed(this, 100);
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mStatusChecker != null)
            if (mStatusMaker != null)
                mStatusChecker.removeCallbacks(mStatusMaker);
    }

    @Override
    public void onBackPressed() {
//        AlertDialog.Builder ab = new AlertDialog.Builder(this);
////        ab.setMessage(Html.fromHtml("<strong><font color=\"#ff0000\"> " + "Html 표현여부 "
////                + "</font></strong><br>HTML 이 제대로 표현되는지 본다."));
//        ab.setMessage(R.string.close_playing_message);
//        ab.setPositiveButton("OK", null);
//        ab.show();
//
//        super.onBackPressed();
//
//        return;

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.app_name);
        alert.setMessage(R.string.close_playing_message);

        alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
            }
        });
        alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.show();
    }

    private void determineBackgroundView(int index) {
        int leftIndex = (index + 2) % 3;
        int rightIndex = (index + 1) % 3;

        switch (index) {
            case 0:
            case 1:
            case 2:
                ivCurrentBg = ivBackgroundList.get(index);
                ivLowerBg = ivBackgroundList.get(index + 3);
                ivLeftBg = ivBackgroundList.get(leftIndex);
                ivRightBg = ivBackgroundList.get(rightIndex);
                ivLowerLeftBg = ivBackgroundList.get(leftIndex + 3);
                ivLowerRightBg = ivBackgroundList.get(rightIndex + 3);

                break;
            case 3:
            case 4:
            case 5:
                ivCurrentBg = ivBackgroundList.get(index);
                ivLowerBg = ivBackgroundList.get((index + 3) % 6);
                ivLeftBg = ivBackgroundList.get(leftIndex + 3);
                ivRightBg = ivBackgroundList.get(rightIndex + 3);
                ivLowerLeftBg = ivBackgroundList.get((leftIndex + 3) % 6);
                ivLowerRightBg = ivBackgroundList.get((rightIndex + 3) % 6);

                break;
        }
    }
}
