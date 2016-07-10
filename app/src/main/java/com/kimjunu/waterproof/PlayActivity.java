package com.kimjunu.waterproof;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private LinearLayout llMoverContainer = null;
    private TextView tvLifePoint = null;

    private Handler mStatusChecker = null;
    private Runnable mStatusMaker = null;

    private boolean isPlayStarted = false;
    private boolean isAccelerating = false;
    private boolean isTurnLeft = false;
    private boolean isTurnRight = false;

    private int screenWidth = 0;
    private int screenHeight = 0;
    private float walkPoint = 0;

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
        tvLifePoint = (TextView) findViewById(R.id.tvLifePoint);

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

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

                    if (!isTurnRight && !isTurnLeft) {
                        if (mPlayer.getAngle() < 0)
                            mPlayer.turnLeft();
                        else if (mPlayer.getAngle() > 0)
                            mPlayer.turnRight();
                    }
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
                    if (screenHeight / 8 < ivPlayer.getY()) {
                        ivPlayer.animate()
                                .translationYBy(-walkPoint * 3)
                                .setDuration(80)
                                .start();
                    } else {
                        ivPlayer.setY(screenHeight / 8);
                    }
                }

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
}
