package com.example.smarthome;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.example.smarthome.AppConfig;
import com.example.smarthome.DeviceState;
import com.example.smarthome.HistoryPoint;
import com.example.smarthome.ModeConfig;
import com.example.smarthome.SensorData;
import com.example.smarthome.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Firebase
    private FirebaseHelper firebase;

    // Header Views
    private TextView tvDayNight, tvLuxHeader, tvFireStatus;
    private CardView cvFirePill;

    // Environment Card
    private TextView tvTemperature, tvHumidity, tvLux, tvDayEval;
    private LineChart chartEnvironment;

    // Lights Card
    private MaterialButtonToggleGroup toggleLightsMode;
    private Button btnLightsAuto, btnLightsManual;
    private View lightsAutoPane, lightsManualPane;
    private Slider sliderLuxThreshold;
    private TextView tvLuxThreshold, tvLightsAutoDesc;
    private SwitchMaterial switchLight1, switchLight2;

    // Rack Card
    private MaterialButtonToggleGroup toggleRackMode;
    private Button btnRackAuto, btnRackManual;
    private TextView rackAutoPane, tvRackStatus;
    private View rackManualPane;
    private Button btnRackOpen, btnRackClose;

    // Fan Card
    private MaterialButtonToggleGroup toggleFanMode;
    private Button btnFanAuto, btnFanManual;
    private TextView fanAutoPane;
    private View fanManualPane;
    private SwitchMaterial switchFan;
    private TextView tvFanStatus;

    // Door Card
    private SwitchMaterial switchDoor;
    private TextView tvDoorStatus;

    // Fire Card
    private CardView cvFireTag;
    private TextView tvFireTag, tvFlameValue, tvFlameDetected;

    // Rain Card
    private CardView cvRainTag;
    private TextView tvRainTag;

    // Gas Card
    private TextView tvGasValue, tvGasStatus, tvGasWarning;

    // State
    private SensorData currentSensor = new SensorData();
    private DeviceState currentDevice = new DeviceState();
    private ModeConfig currentMode = new ModeConfig(1, 1, 1);
    private int luxThreshold = 300;
    private List<HistoryPoint> historyData = new ArrayList<>();
    private static final int MAX_HISTORY = 60;

    // Flags to prevent feedback loops
    private boolean isUpdatingUI = false;
    private boolean isGasAlertShowing = false;
    private boolean isFireAlertShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebase = FirebaseHelper.getInstance();

        initViews();
        setupListeners();
        setupFirebaseListeners();
        setupChart();
    }

    private void initViews() {
        // Header
        tvDayNight = findViewById(R.id.tvDayNight);
        tvLuxHeader = findViewById(R.id.tvLuxHeader);
        tvFireStatus = findViewById(R.id.tvFireStatus);
        cvFirePill = findViewById(R.id.cvFirePill);

        // Environment
        tvTemperature = findViewById(R.id.tvTemperature);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvLux = findViewById(R.id.tvLux);
        tvDayEval = findViewById(R.id.tvDayEval);
        chartEnvironment = findViewById(R.id.chartEnvironment);

        // Lights
        toggleLightsMode = findViewById(R.id.toggleLightsMode);
        btnLightsAuto = findViewById(R.id.btnLightsAuto);
        btnLightsManual = findViewById(R.id.btnLightsManual);
        lightsAutoPane = findViewById(R.id.lightsAutoPane);
        lightsManualPane = findViewById(R.id.lightsManualPane);
        sliderLuxThreshold = findViewById(R.id.sliderLuxThreshold);
        tvLuxThreshold = findViewById(R.id.tvLuxThreshold);
        tvLightsAutoDesc = findViewById(R.id.tvLightsAutoDesc);
        switchLight1 = findViewById(R.id.switchLight1);
        switchLight2 = findViewById(R.id.switchLight2);

        // Rack
        toggleRackMode = findViewById(R.id.toggleRackMode);
        btnRackAuto = findViewById(R.id.btnRackAuto);
        btnRackManual = findViewById(R.id.btnRackManual);
        rackAutoPane = findViewById(R.id.rackAutoPane);
        rackManualPane = findViewById(R.id.rackManualPane);
        tvRackStatus = findViewById(R.id.tvRackStatus);
        btnRackOpen = findViewById(R.id.btnRackOpen);
        btnRackClose = findViewById(R.id.btnRackClose);

        // Fan
        toggleFanMode = findViewById(R.id.toggleFanMode);
        btnFanAuto = findViewById(R.id.btnFanAuto);
        btnFanManual = findViewById(R.id.btnFanManual);
        fanAutoPane = findViewById(R.id.fanAutoPane);
        fanManualPane = findViewById(R.id.fanManualPane);
        switchFan = findViewById(R.id.switchFan);
        tvFanStatus = findViewById(R.id.tvFanStatus);

        // Door
        switchDoor = findViewById(R.id.switchDoor);
        tvDoorStatus = findViewById(R.id.tvDoorStatus);

        // Fire
        cvFireTag = findViewById(R.id.cvFireTag);
        tvFireTag = findViewById(R.id.tvFireTag);
        tvFlameValue = findViewById(R.id.tvFlameValue);
        tvFlameDetected = findViewById(R.id.tvFlameDetected);

        // Rain
        cvRainTag = findViewById(R.id.cvRainTag);
        tvRainTag = findViewById(R.id.tvRainTag);

        // Gas
        tvGasValue = findViewById(R.id.tvGasValue);
        tvGasStatus = findViewById(R.id.tvGasStatus);
        tvGasWarning = findViewById(R.id.tvGasWarning);
    }

    private void setupListeners() {
        // Lux Threshold Slider
        sliderLuxThreshold.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser && !isUpdatingUI) {
                luxThreshold = (int) value;
                tvLuxThreshold.setText(String.valueOf(luxThreshold));
                tvLightsAutoDesc.setText(String.format(Locale.getDefault(),
                        getString(R.string.lights_auto_desc), luxThreshold));
                firebase.setLuxThreshold(luxThreshold);
            }
        });

        // Lights Mode Toggle
        toggleLightsMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked && !isUpdatingUI) {
                if (checkedId == R.id.btnLightsAuto) {
                    firebase.setLightMode(true);
                } else if (checkedId == R.id.btnLightsManual) {
                    firebase.setLightMode(false);
                }
            }
        });

        // Light Switches
        switchLight1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isUpdatingUI && buttonView.isPressed()) {
                firebase.setLight1(isChecked);
            }
        });

        switchLight2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isUpdatingUI && buttonView.isPressed()) {
                firebase.setLight2(isChecked);
            }
        });

        // Rack Mode Toggle
        toggleRackMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked && !isUpdatingUI) {
                if (checkedId == R.id.btnRackAuto) {
                    firebase.setRackMode(true);
                } else if (checkedId == R.id.btnRackManual) {
                    firebase.setRackMode(false);
                }
            }
        });

        // Rack Buttons
        btnRackOpen.setOnClickListener(v -> firebase.setRack(true));
        btnRackClose.setOnClickListener(v -> firebase.setRack(false));

        // Fan Mode Toggle
        toggleFanMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked && !isUpdatingUI) {
                if (checkedId == R.id.btnFanAuto) {
                    firebase.setFanMode(true);
                } else if (checkedId == R.id.btnFanManual) {
                    firebase.setFanMode(false);
                }
            }
        });

        // Fan Switch
        switchFan.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isUpdatingUI && buttonView.isPressed()) {
                firebase.setFan(isChecked);
            }
        });

        // Door Switch
        switchDoor.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isUpdatingUI && buttonView.isPressed()) {
                firebase.setDoor(isChecked);
            }
        });
    }

    private void setupFirebaseListeners() {
        // Listen to Config/luxTh
        firebase.getLuxThresholdRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Long value = snapshot.getValue(Long.class);
                    if (value != null) {
                        luxThreshold = value.intValue();
                        updateLuxThresholdUI();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        // Listen to Sensor data
        firebase.getSensorRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentSensor = snapshot.getValue(SensorData.class);
                if (currentSensor != null) {
                    // Add to history
                    historyData.add(new HistoryPoint(
                            currentSensor.getTemp(),
                            currentSensor.getHumi(),
                            System.currentTimeMillis()
                    ));
                    if (historyData.size() > MAX_HISTORY) {
                        historyData.remove(0);
                    }
                    updateSensorUI();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        // Listen to Mode
        firebase.getModeRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentMode = snapshot.getValue(ModeConfig.class);
                if (currentMode != null) {
                    updateModeUI();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        // Listen to Device
        firebase.getDeviceRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentDevice = snapshot.getValue(DeviceState.class);
                if (currentDevice != null) {
                    updateDeviceUI();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void updateLuxThresholdUI() {
        isUpdatingUI = true;
        sliderLuxThreshold.setValue(luxThreshold);
        tvLuxThreshold.setText(String.valueOf(luxThreshold));
        tvLightsAutoDesc.setText(String.format(Locale.getDefault(),
                getString(R.string.lights_auto_desc), luxThreshold));
        isUpdatingUI = false;
    }

    private void updateSensorUI() {
        // Temperature & Humidity
        tvTemperature.setText(String.format(Locale.getDefault(), "%.1f", currentSensor.getTemp()));
        tvHumidity.setText(String.format(Locale.getDefault(), "%.0f", currentSensor.getHumi()));

        // Lux
        int lux = (int) currentSensor.getLux();
        tvLux.setText(String.valueOf(lux));
        tvLuxHeader.setText(String.valueOf(lux));

        // Day/Night
        boolean isDark = lux < luxThreshold;
        String dayNightText = isDark ? getString(R.string.day_evening) : getString(R.string.day_morning);
        tvDayNight.setText(dayNightText);
        tvDayEval.setText(dayNightText);

        // Fire Status
        boolean isFire = currentSensor.isFire();
        tvFireStatus.setText(isFire ? getString(R.string.fire_detected) : getString(R.string.fire_normal));
        int fireColor = isFire ? R.color.pill_danger_bg : R.color.pill_bg;
        cvFirePill.setCardBackgroundColor(ContextCompat.getColor(this, fireColor));

        tvFireTag.setText(isFire ? getString(R.string.fire_detected) : getString(R.string.fire_safe));
        cvFireTag.setCardBackgroundColor(ContextCompat.getColor(this, fireColor));

        tvFlameValue.setText(String.valueOf(currentSensor.getFlame()));
        tvFlameDetected.setText(isFire ? getString(R.string.flame_yes) : getString(R.string.flame_no));

        // Show FIRE alert if detected
        if (isFire && !isFireAlertShowing) {
            showFireAlert();
        }

        // Rain Status
        boolean isRain = currentSensor.isRaining();
        tvRainTag.setText(isRain ? getString(R.string.rain_raining) : getString(R.string.rain_not_raining));
        int rainColor = isRain ? R.color.pill_danger_bg : R.color.pill_bg;
        cvRainTag.setCardBackgroundColor(ContextCompat.getColor(this, rainColor));

        // Gas Status
        float gasValue = currentSensor.getGas();
        tvGasValue.setText(String.format(Locale.US, "%.0f", gasValue));
        boolean isGasDanger = currentSensor.isGasDanger();
        tvGasStatus.setText(isGasDanger ? getString(R.string.gas_danger) : getString(R.string.gas_safe));
        tvGasStatus.setTextColor(ContextCompat.getColor(this, isGasDanger ? R.color.danger : R.color.ink));
        tvGasWarning.setVisibility(isGasDanger ? View.VISIBLE : View.GONE);

        // Show GAS alert if danger detected
        if (isGasDanger && !isGasAlertShowing) {
            showGasAlert();
        }

        // Update rack auto description
        String rainStatus = isRain ? getString(R.string.rain_yes) : getString(R.string.rain_no);
        rackAutoPane.setText(String.format(Locale.getDefault(),
                getString(R.string.rack_auto_desc), rainStatus));

        // Update chart
        updateChart();
    }

    private void updateModeUI() {
        isUpdatingUI = true;

        // Lights Mode
        if (currentMode.isLightAuto()) {
            toggleLightsMode.check(R.id.btnLightsAuto);
            lightsAutoPane.setVisibility(View.VISIBLE);
            lightsManualPane.setVisibility(View.GONE);
        } else {
            toggleLightsMode.check(R.id.btnLightsManual);
            lightsAutoPane.setVisibility(View.GONE);
            lightsManualPane.setVisibility(View.VISIBLE);
        }

        // Rack Mode
        if (currentMode.isRackAuto()) {
            toggleRackMode.check(R.id.btnRackAuto);
            rackAutoPane.setVisibility(View.VISIBLE);
            rackManualPane.setVisibility(View.GONE);
        } else {
            toggleRackMode.check(R.id.btnRackManual);
            rackAutoPane.setVisibility(View.GONE);
            rackManualPane.setVisibility(View.VISIBLE);
        }

        // Fan Mode
        if (currentMode.isFanAuto()) {
            toggleFanMode.check(R.id.btnFanAuto);
            fanAutoPane.setVisibility(View.VISIBLE);
            fanManualPane.setVisibility(View.GONE);
        } else {
            toggleFanMode.check(R.id.btnFanManual);
            fanAutoPane.setVisibility(View.GONE);
            fanManualPane.setVisibility(View.VISIBLE);
        }

        isUpdatingUI = false;
    }

    private void updateDeviceUI() {
        isUpdatingUI = true;

        if (currentDevice.getLight() != null) {
            switchLight1.setChecked(currentDevice.getLight().isLight1On());
            switchLight2.setChecked(currentDevice.getLight().isLight2On());
        }

        // Rack - hiển thị theo mode
        boolean isRackOpen;
        if (currentMode.isRackAuto()) {
            // Auto mode: hiển thị theo rain sensor (mưa = đóng, không mưa = mở)
            isRackOpen = !currentSensor.isRaining();
        } else {
            // Manual mode: hiển thị theo Firebase device state
            isRackOpen = currentDevice.isRackOpen();
        }
        tvRackStatus.setText(isRackOpen ?
                getString(R.string.rack_open) : getString(R.string.rack_closed));

        // Fan
        switchFan.setChecked(currentDevice.isFanOn());
        tvFanStatus.setText(currentDevice.isFanOn() ? "BẬT" : "TẮT");

        // Door
        switchDoor.setChecked(currentDevice.isDoorOpen());
        tvDoorStatus.setText(currentDevice.isDoorOpen() ?
                getString(R.string.door_open) : getString(R.string.door_closed));

        isUpdatingUI = false;
    }

    private void setupChart() {
        chartEnvironment.getDescription().setEnabled(false);
        chartEnvironment.setTouchEnabled(false);
        chartEnvironment.setDragEnabled(false);
        chartEnvironment.setScaleEnabled(false);
        chartEnvironment.getLegend().setEnabled(false);
        chartEnvironment.setDrawGridBackground(false);

        // X Axis
        XAxis xAxis = chartEnvironment.getXAxis();
        xAxis.setEnabled(false);

        // Left Y Axis (Temperature 20-60°C)
        YAxis leftAxis = chartEnvironment.getAxisLeft();
        leftAxis.setAxisMinimum(20f);
        leftAxis.setAxisMaximum(60f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#1AFFFFFF"));
        leftAxis.setTextColor(Color.parseColor("#96A1BB"));

        // Right Y Axis (Humidity 30-100%)
        YAxis rightAxis = chartEnvironment.getAxisRight();
        rightAxis.setAxisMinimum(30f);
        rightAxis.setAxisMaximum(100f);
        rightAxis.setDrawGridLines(false);
        rightAxis.setTextColor(Color.parseColor("#96A1BB"));
    }

    private void updateChart() {
        if (historyData.size() < 2) return;

        List<Entry> tempEntries = new ArrayList<>();
        List<Entry> humiEntries = new ArrayList<>();

        for (int i = 0; i < historyData.size(); i++) {
            HistoryPoint point = historyData.get(i);
            tempEntries.add(new Entry(i, point.getTemperature()));
            humiEntries.add(new Entry(i, point.getHumidity()));
        }

        // Temperature line
        LineDataSet tempDataSet = new LineDataSet(tempEntries, "Temperature");
        tempDataSet.setColor(Color.parseColor("#6AA2FF"));
        tempDataSet.setLineWidth(2f);
        tempDataSet.setDrawCircles(false);
        tempDataSet.setDrawValues(false);
        tempDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        // Humidity line
        LineDataSet humiDataSet = new LineDataSet(humiEntries, "Humidity");
        humiDataSet.setColor(Color.parseColor("#00C389"));
        humiDataSet.setLineWidth(2f);
        humiDataSet.setDrawCircles(false);
        humiDataSet.setDrawValues(false);
        humiDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);

        LineData lineData = new LineData(tempDataSet, humiDataSet);
        chartEnvironment.setData(lineData);
        chartEnvironment.invalidate();
    }

    private void showGasAlert() {
        isGasAlertShowing = true;

        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle("🚨 CẢNH BÁO KHÍ GAS!")
                .setMessage("⚠️ PHÁT HIỆN KHÍ GAS VƯỢT NGƯỠNG!\n\n" +
                        "💨 Nồng độ Gas: " + String.format(Locale.US, "%.0f", currentSensor.getGas()) + " ppm\n" +
                        "⚠️ Ngưỡng an toàn: 2048 ppm\n\n" +
                        "⚡ Hành động ngay:\n" +
                        "• Tắt nguồn lửa\n" +
                        "• Mở cửa thông gió\n" +
                        "• Sơ tán nếu cần thiết")
                .setPositiveButton("ĐÃ HIỂU", (dialog, which) -> {
                    isGasAlertShowing = false;
                    dialog.dismiss();
                })
                .setOnDismissListener(dialog -> isGasAlertShowing = false)
                .setCancelable(false)
                .show();
    }

    private void showFireAlert() {
        isFireAlertShowing = true;

        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle("🔥 CẢNH BÁO CHÁY!")
                .setMessage("⚠️ PHÁT HIỆN LỬA TRONG NHÀ!\n\n" +
                        "🔥 Trạng thái: ĐANG CHÁY\n" +
                        "🚨 Mức độ: NGUY HIỂM\n\n" +
                        "⚡ Hành động NGAY:\n" +
                        "• Báo động mọi người\n" +
                        "• Sơ tán khỏi khu vực\n" +
                        "• Gọi cứu hỏa: 114")
                .setPositiveButton("ĐÃ HIỂU", (dialog, which) -> {
                    isFireAlertShowing = false;
                    dialog.dismiss();
                })
                .setOnDismissListener(dialog -> isFireAlertShowing = false)
                .setCancelable(false)
                .show();
    }
}

