package com.kimjunu.waterproof;

public class Player {
    private float lifePoint;
    private float consumePoint;
    private float currentSpeed;
    private float acceleratePoint;
    private float speedLimit;
    private float angle; // Left: +, Right: -, Neutral: 0
    private float controlPoint; // Angle Control point

    public Player() {
        lifePoint = 1000;
        consumePoint = (float) 0.5;
        currentSpeed = 0;
        acceleratePoint = (float) 0.25;
        speedLimit = 15;
        angle = 0;
        controlPoint = (float) 0.5;
    }

    public void accelerate() {
        if (currentSpeed < speedLimit)
            currentSpeed += acceleratePoint;
        else
            currentSpeed = speedLimit;
    }

    public void deaccelerate() {
        if (currentSpeed > 0)
            currentSpeed -= 0.5;
        else
            currentSpeed = 0;
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
        float resultPoint = lifePoint + point;

        if (resultPoint > 1000)
            lifePoint = 1000;
        else
            lifePoint += point;
    }

    public float getLifePoint() {
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
