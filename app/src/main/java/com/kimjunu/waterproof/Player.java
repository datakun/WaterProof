package com.kimjunu.waterproof;

public class Player {
    private int lifePoint;
    private int consumePoint;
    private float currentSpeed;
    private float acceleratePoint;
    private float angle; // Left: +, Right: -, Neutral: 0
    private float controlPoint;

    public Player() {
        lifePoint = 1000;
        consumePoint = 1;
        currentSpeed = 0;
        acceleratePoint = (float) 0.5;
        angle = 0;
        controlPoint = 1;
    }

    public void accelerate() {
        if (currentSpeed < 10)
            currentSpeed += acceleratePoint;
    }

    public void deaccelerate() {
        if (currentSpeed > 0)
            currentSpeed -= 1;
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

    public String getStatus() {
        String status = "";
        status += "Speed : " + Float.toString(currentSpeed) + ", ";
        status += "Angle : " + Float.toString(angle);

        return status;
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
