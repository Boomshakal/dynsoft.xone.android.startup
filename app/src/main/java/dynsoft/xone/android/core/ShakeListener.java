package dynsoft.xone.android.core;

import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.content.Context;

public class ShakeListener implements SensorListener {
    
    private static final int FORCE_THRESHOLD = 3000;
    private static final int TIME_THRESHOLD = 100;
    private static final int SHAKE_TIMEOUT = 500;
    private static final int SHAKE_DURATION = 500;
    private static final int SHAKE_COUNT = 3;

    private SensorManager _sensorMgr;
    private OnShakeListener _shakeListener;
    private float _lastX = -1.0f;
    private float _lastY = -1.0f;
    private float _lastZ = -1.0f;
    private long _lastTime;
    private Context _context;
    private int _shakeCount = 0;
    private long _lastShake;
    private long _lastForce;

    public interface OnShakeListener {
        public void onShake();
    }

    public ShakeListener(Context context) {
        _context = context;
        resume();
    }

    public void setOnShakeListener(OnShakeListener listener) {
        _shakeListener = listener;
    }

    public void resume() {
        _sensorMgr = (SensorManager)_context.getSystemService(Context.SENSOR_SERVICE);
        if (_sensorMgr != null) {
            boolean supported = _sensorMgr.registerListener(this, SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_GAME);
            if (supported == false) {
                _sensorMgr.unregisterListener(this, SensorManager.SENSOR_ACCELEROMETER);
            }
        }
    }

    public void pause() {
        if (_sensorMgr != null) {
            _sensorMgr.unregisterListener(this, SensorManager.SENSOR_ACCELEROMETER);
            _sensorMgr = null;
        }
    }

    public void onAccuracyChanged(int sensor, int accuracy) {
    }

    public void onSensorChanged(int sensor, float[] values) {
        
        if (sensor != SensorManager.SENSOR_ACCELEROMETER)
            return;
        
        long now = System.currentTimeMillis();

        if ((now - _lastForce) > SHAKE_TIMEOUT) {
            _shakeCount = 0;
        }

        if ((now - _lastTime) > TIME_THRESHOLD) {
            long diff = now - _lastTime;
            float deltaX = values[SensorManager.DATA_X] - _lastX;
            float deltaY = values[SensorManager.DATA_X] - _lastY;
            float deltaZ = values[SensorManager.DATA_X] - _lastZ;
            
            //float speed = Math.abs(values[SensorManager.DATA_X] + values[SensorManager.DATA_Y] + values[SensorManager.DATA_Z] - mLastX - mLastY - mLastZ) / diff * 10000;
            //float speed = (float) (Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ)/diff * 10000);
            
            //if (speed > FORCE_THRESHOLD) {
            if((Math.abs(values[0])>14||Math.abs(values[1])>14||Math.abs(values[2])>14)){
                if ((++_shakeCount >= SHAKE_COUNT) && (now - _lastShake > SHAKE_DURATION)) {
                    _lastShake = now;
                    _shakeCount = 0;
                    if (_shakeListener != null) {
                        _shakeListener.onShake();
                    }
                }
                _lastForce = now;
            }
            _lastTime = now;
            _lastX = values[SensorManager.DATA_X];
            _lastY = values[SensorManager.DATA_Y];
            _lastZ = values[SensorManager.DATA_Z];
        }
    }
}
