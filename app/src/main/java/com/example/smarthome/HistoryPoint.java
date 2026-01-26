package com.example.smarthome;

/**
 * Model class for storing history data points for chart
 */
public class HistoryPoint {
    private float temperature;
    private float humidity;
    private long timestamp;

    public HistoryPoint(float temperature, float humidity, long timestamp) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.timestamp = timestamp;
    }

    public float getTemperature() { return temperature; }
    public float getHumidity() { return humidity; }
    public long getTimestamp() { return timestamp; }

    public void setTemperature(float temperature) { this.temperature = temperature; }
    public void setHumidity(float humidity) { this.humidity = humidity; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

