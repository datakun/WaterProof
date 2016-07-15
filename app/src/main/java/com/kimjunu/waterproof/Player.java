package com.kimjunu.waterproof;

public class Player {
    private int lifePoint;
    private int consumePoint;
    private float currentSpeed;
    private float acceleratePoint;
    private float speedLimit;
    private float angle; // Left: +, Right: -, Neutral: 0
    private float controlPoint; // Angle Control point

    public Player() {
        lifePoint = 1000;
        consumePoint = 1;
        currentSpeed = 0;
        acceleratePoint = (float) 0.5;
        speedLimit = 15;
        angle = 0;
        controlPoint = 1;
    }

    public void accelerate() {
        if (currentSpeed < speedLimit)
            currentSpeed += acceleratePoint;
    }

    public void deaccelerate() {
        if (currentSpeed > 0)
            currentSpeed -= 1;
    }

    public float getSpeedLimit() {
        return speedLimit;
    }

    public void setCurrentSpeed(float limit) {
        speedLimit = limit;
    }

    public void consumeLife() {
        if (lifePoint > 0)
            lifePoint -= consumePoint;
        else
            lifePoint = 0;
    }

    public void gainLife(int point) {
        int resultPoint = lifePoint + point;

        if (resultPoint > 1000)
            lifePoint = 1000;
        else
            lifePoint += point;
    }

    public int getLifePoint() {
        return lifePoint;
    }

    public void turnLeft() {
        if (angle < 80)
            angle += controlPoint;
    }

    public void turnRight() {
        if (angle > -80)
            angle -= controlPoint;
    }

    public float getAcceleratePoint() {
        return acceleratePoint;
    }

    public float getCurrentSpeed() {
        return currentSpeed;
    }

    public float getAngle() {
        return angle;
    }
}
