package com.example.smarthome;

/**
 * Model class for Device states from Firebase
 * Maps to Firebase path: /Device
 */
public class DeviceState {
    private LightState light;
    private int rack;  // 0=closed, 1=open
    private int fan;   // 0=off, 1=on
    private int door;  // 0=closed, 1=open

    public DeviceState() {
        // Default constructor required for Firebase
    }

    public DeviceState(LightState light, int rack, int fan, int door) {
        this.light = light;
        this.rack = rack;
        this.fan = fan;
        this.door = door;
    }

    // Getters
    public LightState getLight() { return light; }
    public int getRack() { return rack; }
    public int getFan() { return fan; }
    public int getDoor() { return door; }

    // Setters
    public void setLight(LightState light) { this.light = light; }
    public void setRack(int rack) { this.rack = rack; }
    public void setFan(int fan) { this.fan = fan; }
    public void setDoor(int door) { this.door = door; }

    // Helper methods
    public boolean isRackOpen() { return rack == 1; }
    public boolean isFanOn() { return fan == 1; }
    public boolean isDoorOpen() { return door == 1; }

    /**
     * Nested class for Light state (has 2 lights)
     */
    public static class LightState {
        private int light1;  // 0=off, 1=on
        private int light2;  // 0=off, 1=on

        public LightState() {}

        public LightState(int light1, int light2) {
            this.light1 = light1;
            this.light2 = light2;
        }

        public int getLight1() { return light1; }
        public int getLight2() { return light2; }
        public void setLight1(int light1) { this.light1 = light1; }
        public void setLight2(int light2) { this.light2 = light2; }

        public boolean isLight1On() { return light1 == 1; }
        public boolean isLight2On() { return light2 == 1; }
    }

    @Override
    public String toString() {
        return "DeviceState{" +
                "light=" + light +
                ", rack=" + rack +
                ", fan=" + fan +
                ", door=" + door +
                '}';
    }
}

