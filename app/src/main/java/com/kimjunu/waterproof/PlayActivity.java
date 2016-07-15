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
    private TextView tvTime = null;
    private LinearLayout llDepthContainer = null;
    private TextView tvDepth = null;

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

    private long startTime = 0;
    private long depth = 0;

    private boolean gamePause = false;

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
        tvTime = (TextView) findViewById(R.id.tvTime);
        llDepthContainer = (LinearLayout) findViewById(R.id.llDepthContainer);
        tvDepth = (TextView) findViewById(R.id.tvDepth);

        ivBackgroundList = new ArrayList<>();
        ivBackgroundList.add((ImageView) findViewById(R.id.ivBg1));
        ivBackgroundList.add((ImageView) findViewById(R.id.ivBg2));
        ivBackgroundList.add((ImageView) findViewById(R.id.ivBg3));
        ivBackgroundList.add((ImageView) findViewById(R.id.ivBg4));
        ivBackgroundList.add((ImageView) findViewById(R.id.ivBg5));
        ivBackgroundList.add((ImageView) findViewById(R.id.ivBg6));

        llMoverContainer.setVisibility(View.INVISIBLE);
        llLifeContainer.setVisibility(View.INVISIBLE);
        llDepthContainer.setVisibility(View.INVISIBLE);

        tvTime.setVisibility(View.INVISIBLE);

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

        determineBackgroundView(1);

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

        walkPoint = (float) (screenHeight / 2000.0);

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
                        mStatusChecker.postDelayed(mStatusMaker, 50);

                        llMoverContainer.setVisibility(View.VISIBLE);
                        llLifeContainer.setVisibility(View.VISIBLE);
                        llDepthContainer.setVisibility(View.VISIBLE);

                        tvTime.setVisibility(View.VISIBLE);

                        startTime = System.currentTimeMillis();

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
                        .setDuration(30)
                        .start();

                // 전진 애니메이션
                float movingPoint = mPlayer.getCurrentSpeed() * walkPoint;
                float turnPoint = mPlayer.getAngle() / 80 * movingPoint;
                movingPoint = movingPoint - ((Math.abs(mPlayer.getAngle()) / 10) * walkPoint);
                if (movingPoint < 0)
                    movingPoint = 0;

                if (mPlayer.getCurrentSpeed() > 0) {
                    if (screenHeight / 5 > ivPlayer.getY()) {
                        ivPlayer.animate()
                                .translationYBy(movingPoint)
                                .setDuration(40)
                                .start();
                    } else {
                        ivPlayer.setY(screenHeight / 5);
                    }
                } else {
                    if (screenHeight / 7 < ivPlayer.getY()) {
                        ivPlayer.animate()
                                .translationYBy(-walkPoint * 3)
                                .setDuration(40)
                                .start();

                        // 배경 이동 애니메이션
                        ivCurrentBg.setY(ivCurrentBg.getY() - walkPoint * 3);
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

                    if (currentRect.contains((int) ivPlayer.getX(), (int) ivPlayer.getY())) {
                        if (item.getY() <= 0 &&
                                (item.getX() + item.getWidth() >= screenWidth || item.getX() >= 0))
                            break;

                        if (item.getY() <= 0)
                            break;
                    }
                }

                determineBackgroundView(index);

                // 배경 이동 애니메이션
                ivCurrentBg.setY(ivCurrentBg.getY() - movingPoint);
                ivCurrentBg.setX(ivCurrentBg.getX() + turnPoint);

                ivLeftBg.setY(ivCurrentBg.getY());
                ivRightBg.setY(ivCurrentBg.getY());

                ivLeftBg.setX(ivCurrentBg.getX() - imageWidth + 5);
                ivRightBg.setX(ivCurrentBg.getX() + imageWidth - 5);

                ivLowerLeftBg.setY(ivLeftBg.getY() + imageWidth - 5);
                ivLowerBg.setY(ivCurrentBg.getY() + imageWidth - 5);
                ivLowerRightBg.setY(ivRightBg.getY() + imageWidth - 5);

                ivLowerLeftBg.setX(ivCurrentBg.getX() - imageWidth + 5);
                ivLowerBg.setX(ivCurrentBg.getX());
                ivLowerRightBg.setX(ivCurrentBg.getX() + imageWidth - 5);

                // 라이프 소비 애니메이션
                String lifePoint = String.valueOf((int) mPlayer.getLifePoint() / 10) + " %";
                tvLifePoint.setText(lifePoint);

                float percent = (mPlayer.getLifePoint() / (float) 1000.0) * 0xff;

                Drawable drawable = ivLifePoint.getDrawable();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    VectorDrawable vectorDrawable = (VectorDrawable) drawable;
                    assert vectorDrawable != null;
                    vectorDrawable.setAlpha((int) percent);

                    ivLifePoint.setImageDrawable(vectorDrawable);
                }

                // 시간 표시
                long sec = (System.currentTimeMillis() - startTime) / 1000;
                long min = sec / 60;
                sec = sec % 60;

                String timeString = String.format("%02d : %02d", min, sec);
                tvTime.setText(timeString);

                // 깊이 표시
                depth += movingPoint / walkPoint;

                String depthString = String.valueOf(depth / 200) + " m";
                tvDepth.setText(depthString);

                if (!gamePause)
                    mStatusChecker.postDelayed(this, 50);
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();

        if (gamePause)
            mStatusChecker.postDelayed(mStatusMaker, 50);

        gamePause = false;
    }

    @Override
    public void onStop() {
        super.onStop();

        gamePause = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mStatusChecker != null)
            if (mStatusMaker != null)
                mStatusChecker.removeCallbacks(mStatusMaker);
    }

    @Override
    public void onBackPressed() {
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
        int leftIndex;
        int rightIndex;

        switch (index) {
            case 0:
            case 1:
            case 2:
                leftIndex = (index + 2) % 3;
                rightIndex = (index + 1) % 3;

                ivLeftBg = ivBackgroundList.get(leftIndex);
                ivCurrentBg = ivBackgroundList.get(index);
                ivRightBg = ivBackgroundList.get(rightIndex);
                ivLowerLeftBg = ivBackgroundList.get(leftIndex + 3);
                ivLowerBg = ivBackgroundList.get(index + 3);
                ivLowerRightBg = ivBackgroundList.get(rightIndex + 3);

                break;
            case 3:
            case 4:
            case 5:
                leftIndex = (index + 2) % 3 + 3;
                rightIndex = (index + 1) % 3 + 3;

                ivLeftBg = ivBackgroundList.get(leftIndex);
                ivCurrentBg = ivBackgroundList.get(index);
                ivRightBg = ivBackgroundList.get(rightIndex);
                ivLowerLeftBg = ivBackgroundList.get((leftIndex + 3) % 6);
                ivLowerBg = ivBackgroundList.get((index + 3) % 6);
                ivLowerRightBg = ivBackgroundList.get((rightIndex + 3) % 6);

                break;
        }
    }
}
