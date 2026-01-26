package com.example.smarthome;

/**
 * Model class for App configuration from Firebase
 * Maps to Firebase path: /Config
 */
public class AppConfig {
    private int luxTh;  // Light threshold (lux)

    public AppConfig() {
        this.luxTh = 300;  // Default value
    }

    public AppConfig(int luxTh) {
        this.luxTh = luxTh;
    }

    public int getLuxTh() { return luxTh; }
    public void setLuxTh(int luxTh) { this.luxTh = luxTh; }

    @Override
    public String toString() {
        return "AppConfig{luxTh=" + luxTh + '}';
    }
}

