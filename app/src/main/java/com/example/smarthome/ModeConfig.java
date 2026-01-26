package com.example.smarthome;

/**
 * Model class for Mode configuration from Firebase
 * Maps to Firebase path: /Mode
 */
public class ModeConfig {
    private int light;  // 1=auto, 0=manual
    private int rack;   // 1=auto, 0=manual
    private int fan;    // 1=auto, 0=manual

    public ModeConfig() {
        // Default constructor required for Firebase
    }

    public ModeConfig(int light, int rack, int fan) {
        this.light = light;
        this.rack = rack;
        this.fan = fan;
    }

    // Getters
    public int getLight() { return light; }
    public int getRack() { return rack; }
    public int getFan() { return fan; }

    // Setters
    public void setLight(int light) { this.light = light; }
    public void setRack(int rack) { this.rack = rack; }
    public void setFan(int fan) { this.fan = fan; }

    // Helper methods
    public boolean isLightAuto() { return light == 1; }
    public boolean isRackAuto() { return rack == 1; }
    public boolean isFanAuto() { return fan == 1; }

    @Override
    public String toString() {
        return "ModeConfig{" +
                "light=" + (light == 1 ? "auto" : "manual") +
                ", rack=" + (rack == 1 ? "auto" : "manual") +
                ", fan=" + (fan == 1 ? "auto" : "manual") +
                '}';
    }
}

