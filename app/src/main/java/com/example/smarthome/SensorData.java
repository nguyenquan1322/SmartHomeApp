package com.example.smarthome;

/**
 * Model class for Sensor data from Firebase
 * Maps to Firebase path: /Sensor
 */
public class SensorData {
    private float temp;
    private float humi;
    private float lux;
    private int rain;    // 0 or 1
    private int flame;   // 0 or 1

    public SensorData() {
        // Default constructor required for Firebase
    }

    public SensorData(float temp, float humi, float lux, int rain, int flame) {
        this.temp = temp;
        this.humi = humi;
        this.lux = lux;
        this.rain = rain;
        this.flame = flame;
    }

    // Getters
    public float getTemp() { return temp; }
    public float getHumi() { return humi; }
    public float getLux() { return lux; }
    public int getRain() { return rain; }
    public int getFlame() { return flame; }

    // Setters
    public void setTemp(float temp) { this.temp = temp; }
    public void setHumi(float humi) { this.humi = humi; }
    public void setLux(float lux) { this.lux = lux; }
    public void setRain(int rain) { this.rain = rain; }
    public void setFlame(int flame) { this.flame = flame; }

    // Helper methods
    public boolean isRaining() { return rain == 1; }
    public boolean isFire() { return flame == 1; }

    @Override
    public String toString() {
        return "SensorData{" +
                "temp=" + temp +
                ", humi=" + humi +
                ", lux=" + lux +
                ", rain=" + rain +
                ", flame=" + flame +
                '}';
    }
}

