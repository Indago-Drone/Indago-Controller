package com.lewei.multiple.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Sensors {
    public static final int SENSOR_MAX_ROTATE = 70;
    public static final int SENSOR_MIN_ROTATE = 10;
    public static final int SENSOR_ROTATE_LEVEL = 60;
    protected String Tag;
    private Context context;
    private OnSensorValue onSensorValue;
    private Sensor sensor;
    private SensorEventListener sensorEventListener;
    private SensorManager sensorManager;

    /* renamed from: com.lewei.multiple.app.Sensors.1 */
    class C00511 implements SensorEventListener {
        C00511() {
        }

        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == 1) {
                int x = (int) (event.values[0] * 10.0f);
                int y = (int) (event.values[1] * 10.0f);
                if (x > Sensors.SENSOR_MIN_ROTATE) {
                    x -= 10;
                } else if (x < -10) {
                    x += Sensors.SENSOR_MIN_ROTATE;
                } else {
                    x = 0;
                }
                if (y > Sensors.SENSOR_MIN_ROTATE) {
                    y -= 10;
                } else if (y < -10) {
                    y += Sensors.SENSOR_MIN_ROTATE;
                } else {
                    y = 0;
                }
                if (x > Sensors.SENSOR_ROTATE_LEVEL) {
                    x = Sensors.SENSOR_ROTATE_LEVEL;
                }
                if (y > Sensors.SENSOR_ROTATE_LEVEL) {
                    y = Sensors.SENSOR_ROTATE_LEVEL;
                }
                if (x < -60) {
                    x = -60;
                }
                if (y < -60) {
                    y = -60;
                }
                if (Sensors.this.onSensorValue != null) {
                    Sensors.this.onSensorValue.setValue(y, x);
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    public interface OnSensorValue {
        void setValue(int i, int i2);
    }

    public Sensors(Context context) {
        this.sensorManager = null;
        this.sensor = null;
        this.sensorEventListener = null;
        this.Tag = "Sensors";
        this.onSensorValue = null;
        this.context = context;
        Init_Sensor();
    }

    public void setOnSensorValue(OnSensorValue onSensorValue) {
        this.onSensorValue = onSensorValue;
    }

    public void setSpeedLevel(int isHigh) {
    }

    public void register() {
        if (this.sensorManager != null && this.sensorEventListener != null) {
            this.sensorManager.registerListener(this.sensorEventListener, this.sensor, 1);
        }
    }

    public void unregister() {
        if (this.sensorManager != null && this.sensorEventListener != null) {
            this.sensorManager.unregisterListener(this.sensorEventListener);
        }
    }

    private void Init_Sensor() {
        this.sensorManager = (SensorManager) this.context.getSystemService("sensor");
        this.sensor = this.sensorManager.getDefaultSensor(1);
        this.sensorEventListener = new C00511();
    }
}
