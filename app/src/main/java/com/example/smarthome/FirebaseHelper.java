package com.example.smarthome;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Firebase Database helper class
 * Provides easy access to Firebase paths
 */
public class FirebaseHelper {
    private static FirebaseHelper instance;
    private final FirebaseDatabase database;

    // Root references
    private final DatabaseReference configRef;
    private final DatabaseReference sensorRef;
    private final DatabaseReference modeRef;
    private final DatabaseReference deviceRef;

    private FirebaseHelper() {
        // Get Firebase instance (URL được config tự động từ google-services.json)
        database = FirebaseDatabase.getInstance();

        // Enable offline persistence (optional, giúp app hoạt động khi mất mạng)
        try {
            database.setPersistenceEnabled(true);
        } catch (Exception e) {
            // Persistence đã được enable rồi, ignore exception
        }

        configRef = database.getReference("Config");
        sensorRef = database.getReference("Sensor");
        modeRef = database.getReference("Mode");
        deviceRef = database.getReference("Device");
    }

    public static synchronized FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    // Getters for root references
    public DatabaseReference getConfigRef() { return configRef; }
    public DatabaseReference getSensorRef() { return sensorRef; }
    public DatabaseReference getModeRef() { return modeRef; }
    public DatabaseReference getDeviceRef() { return deviceRef; }

    // Config paths
    public DatabaseReference getLuxThresholdRef() {
        return configRef.child("luxTh");
    }

    // Mode paths
    public DatabaseReference getLightModeRef() {
        return modeRef.child("light");
    }

    public DatabaseReference getRackModeRef() {
        return modeRef.child("rack");
    }

    public DatabaseReference getFanModeRef() {
        return modeRef.child("fan");
    }

    // Device paths
    public DatabaseReference getLight1Ref() {
        return deviceRef.child("light").child("light1");
    }

    public DatabaseReference getLight2Ref() {
        return deviceRef.child("light").child("light2");
    }

    public DatabaseReference getRackRef() {
        return deviceRef.child("rack");
    }

    public DatabaseReference getFanRef() {
        return deviceRef.child("fan");
    }

    public DatabaseReference getDoorRef() {
        return deviceRef.child("door");
    }

    // Helper methods to write values
    public void setLuxThreshold(int value) {
        getLuxThresholdRef().setValue(value);
    }

    public void setLightMode(boolean isAuto) {
        getLightModeRef().setValue(isAuto ? 1 : 0);
    }

    public void setRackMode(boolean isAuto) {
        getRackModeRef().setValue(isAuto ? 1 : 0);
    }

    public void setFanMode(boolean isAuto) {
        getFanModeRef().setValue(isAuto ? 1 : 0);
    }

    public void setLight1(boolean on) {
        getLight1Ref().setValue(on ? 1 : 0);
    }

    public void setLight2(boolean on) {
        getLight2Ref().setValue(on ? 1 : 0);
    }

    public void setRack(boolean open) {
        getRackRef().setValue(open ? 1 : 0);
    }

    public void setFan(boolean on) {
        getFanRef().setValue(on ? 1 : 0);
    }

    public void setDoor(boolean open) {
        getDoorRef().setValue(open ? 1 : 0);
    }
}

