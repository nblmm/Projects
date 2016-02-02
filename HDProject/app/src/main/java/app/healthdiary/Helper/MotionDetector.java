package app.healthdiary.Helper;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.widget.Toast;

/**
 * Created by Hongyuan on 1/6/2016.
 */
public class MotionDetector {
    private Context context;

    private SensorManager mSensorManager;
    private Sensor mAccelerationSensor;
    private Sensor mOrientationSensor;
    private Sensor mSignificantMotionSensor;
    private SensorEventListener sensorEventListener;
    private TriggerEventListener mTriggerEventListener;
    private float [] mOrientation = new float [3];
    private final float[] mRotationMatrix = new float[16];
    private LocationCollector mlocationCollector;

    public void setCount(int count) {
        this.count = count;
    }

    public void setmAccelSum(double mAccelSum) {
        this.mAccelSum = mAccelSum;
    }

    private int count = 0;
    //private int stationaryCount = 0;
    //private int lags = 10;
    //sensor sample frequency, microsecond
    private int n_Frequency = 3000000;
    private double mAccelCurrent;

    public double getmAccelSum() {
        return mAccelSum;
    }

    public int getCount() {
        return count;
    }

    private double mAccelSum;

    private Boolean b_Moving = true;
    public Boolean getB_Moving() {
        return b_Moving;
    }

    private Boolean b_Sensor1 = false;
    private Boolean b_Sensor2 = false;
    private Boolean b_Sensor3 = false;
    private Boolean b_Sensor4 = false;
    public Boolean getB_Sensor1() {

        return b_Sensor1;
    }
    /*
    public Boolean getB_Sensor2() {
        return b_Sensor2;
    }

    public Boolean getB_Sensor3() {
        return b_Sensor3;
    }

    public Boolean getB_Sensor4() {
        return b_Sensor4;
    }
    */

    //if the average acceleration force is smaller than the threshold during this duration then static
    private int n_CountToDetermineStatic = 100;
    private int n_CountToDetermineMoving = 60;
    //the threshold for static state
    private float mStaticThreshold = 0.1f;

    public MotionDetector(Context context, LocationCollector locationCollector){
        this.context = context;
        this.mlocationCollector = locationCollector;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }
    /*
    * start sensor manager
    * @para int type type of sensor, 1. Significant Motion Sensor;
    *                                2. Accelerometer;
    *                                3. Rotation Vector Sensor
    *                                4. Accelerometer + Rotation Vector Sensor
    * */
    public void startManager(int type) {
        count = 0;
        mAccelSum = 0.00f;
        mAccelCurrent = 0.00f;
        switch (type){
            case 1:
                mSignificantMotionSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
                mTriggerEventListener = new TriggerEventListener() {
                    @Override
                    /*
                    * when a significant motion detected, an event is triggered to
                    * activate Accelerometer + Rotation Vector Sensor
                    * */
                    public void onTrigger(TriggerEvent event) {
                        ActionOnMoving();
                        b_Moving = true;
                    }
                };
                if(mSignificantMotionSensor != null) {
                    Toast.makeText(context, "find a Significant Motion Sensor!", Toast.LENGTH_SHORT).show();
                    mSensorManager.requestTriggerSensor(mTriggerEventListener, mSignificantMotionSensor);
                }
                //the device does not have a significant motion sensor
                else {
                    //Toast.makeText(this, "Cannot find a Significant Motion Sensor!", Toast.LENGTH_SHORT).show();
                    registerLinearAccelerometer(n_CountToDetermineMoving);
                    b_Sensor1 = true;
                }
                break;
            case 2:
                registerLinearAccelerometer(n_CountToDetermineStatic);
                b_Sensor2 = true;
                break;
            case 3:
                b_Sensor3 = true;
                break;
            case 4:
                mAccelerationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
                mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
                if(mAccelerationSensor != null){
                    // initialize the rotation matrix to identity
                    mRotationMatrix[ 0] = 1;
                    mRotationMatrix[ 4] = 1;
                    mRotationMatrix[ 8] = 1;
                    mRotationMatrix[12] = 1;
                    sensorEventListener = new SensorEventListener() {
                        public void onSensorChanged(SensorEvent event) {
                            float mSensorX, mSensorY, mSensorZ;
                            if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
                                mSensorX = event.values[0];
                                mSensorY = event.values[1];
                                mSensorZ = event.values[2];
                                mAccelCurrent = Math.sqrt(mSensorX * mSensorX + mSensorY * mSensorY + mSensorZ * mSensorZ);
                                mAccelSum += mAccelCurrent;
                                //System.out.println("mAccelCurrent: " + mAccelCurrent);
                                count++;
                                System.out.println("average acce: " + mAccelSum / count);
                                if (count >= n_CountToDetermineStatic)
                                {
                                    b_Moving = (mAccelSum / count) >= mStaticThreshold;
                                    count  = 0;
                                    mAccelSum = 0;
                                }
                            }
                            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
                                SensorManager.getRotationMatrixFromVector(
                                        mRotationMatrix , event.values);
                                SensorManager.getOrientation(mRotationMatrix, mOrientation);
                            }
                        }
                        public void onAccuracyChanged(Sensor sensor, int accuracy) {

                        }
                    };
                    mSensorManager.registerListener(sensorEventListener,mAccelerationSensor,n_Frequency );
                    mSensorManager.registerListener(sensorEventListener,mOrientationSensor,n_Frequency );
                    b_Sensor4 = true;
                }
                break;
            default:
                break;
        }
    }

    private void ActionOnMoving(){
        //moving
        mlocationCollector.startManager(2, true, true);
        mlocationCollector.setB_Moving(true);
        SwitchToSensors(2);
        System.out.println("The subject is moving!!!!!!!");
    }

    private void ActionOnStaying(){
        //staying
        mlocationCollector.startManager(3, false, true);
        mlocationCollector.setB_Moving(false);
        SwitchToSensors(1);
        System.out.println("The subject is staying!!!!!!!");
    }
    private void registerLinearAccelerometer(final int CountThreshold){
        mAccelerationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if(mAccelerationSensor != null) {
            sensorEventListener = new SensorEventListener() {
                public void onSensorChanged(SensorEvent event) {
                    //float mSensorX, mSensorY, mSensorZ;
                    if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                        mAccelCurrent = Math.sqrt(Math.pow(event.values[0], 2) + Math.pow(event.values[1], 2) + Math.pow(event.values[2], 2));
                        System.out.println("mAccelCurrent: " + mAccelCurrent);
                        //get the sum of the acceleration force for the last five minutes
                        mAccelSum += mAccelCurrent;
                        count++;
                        System.out.println("count: " + count);
                        if (count >= CountThreshold)
                        {
                            System.out.println("Average acceleration force: " + mAccelSum / count);
                            Boolean Moving = (mAccelSum / count) >= mStaticThreshold;
                            System.out.println("Moving: " + Moving);
                            if( Moving && !b_Moving) {
                                ActionOnMoving();
                                b_Moving = true;

                            }
                            else if(!Moving && b_Moving){
                                ActionOnStaying();
                                b_Moving = false;
                            }
                            count  = 0;
                            mAccelSum = 0;
                        }
                    }
                }
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };
            mSensorManager.registerListener(sensorEventListener,mAccelerationSensor,n_Frequency);
        }
        //the device does not have a LINEAR_ACCELERATION sensor
        //assume the device is old and do not accept customized sampling frequency for motion sensors
        //use SENSOR_DELAY_NORMAL, 5 Hz.
        else
        {
            final float alpha = 0.8f;
            final double [] gravity =  new double [3];
            mAccelerationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if(mAccelerationSensor != null) {
                sensorEventListener = new SensorEventListener() {
                    public void onSensorChanged(SensorEvent event) {
                        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                            double [] linear_acceleration = new double [3];
                            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

                            linear_acceleration[0] = event.values[0] - gravity[0];
                            linear_acceleration[1] = event.values[1] - gravity[1];
                            linear_acceleration[2] = event.values[2] - gravity[2];
                            mAccelCurrent = Math.sqrt(Math.pow(linear_acceleration[0],2) + Math.pow(linear_acceleration[1],2)  + Math.pow(linear_acceleration[2],2) );
                            System.out.println("mAccelCurrent: " + mAccelCurrent);
                            //get the sum of the acceleration force for the last five minutes
                            mAccelSum += mAccelCurrent;
                            count++;
                            System.out.println("count: " + count);
                            if (count >= 1500)
                            {
                                System.out.println("Average acceleration force: " + mAccelSum / count);
                                Boolean Moving = (mAccelSum / count) >= mStaticThreshold;
                                System.out.println("Moving: " + Moving);
                                if( Moving && !b_Moving) {
                                    ActionOnMoving();
                                    b_Moving = true;
                                }
                                else if(!Moving && b_Moving){
                                    ActionOnStaying();
                                    b_Moving = false;
                                }
                                count  = 0;
                                mAccelSum = 0;
                            }
                        }
                    }
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                };
            }
            mSensorManager.registerListener(sensorEventListener,mAccelerationSensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    /*
    * Switch sensors:
    * @para int type type of sensor, 1. Significant Motion Sensor;
    *                                2. Accelerometer;
    *                                3. Rotation Vector Sensor
    *                                4. Accelerometer + Rotation Vector Sensor
    * */
    public void SwitchToSensors(int startType)
    {
        endService();
        startManager(startType);
        System.out.println("Start Sensor: " + startType);
    }
    /*
    * @para int type type of sensor, 1. Significant Motion Sensor;
    *                                2. Accelerometer;
    *                                3. Rotation Vector Sensor
    *                                4. Accelerometer + Rotation Vector Sensor
     */
    public void endService(){
        if (mSignificantMotionSensor != null) mSensorManager.cancelTriggerSensor(mTriggerEventListener, mSignificantMotionSensor);
        if (mSensorManager != null) mSensorManager.unregisterListener(sensorEventListener);
        b_Sensor1 = false;
        b_Sensor2 = false;
        b_Sensor3 = false;
        b_Sensor4 = false;
    }
}
