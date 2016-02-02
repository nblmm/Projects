package app.healthdiary.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
//import android.os.PowerManager;
import android.util.Log;

import app.healthdiary.Helper.LocationCollector;
import app.healthdiary.Helper.MotionDetector;
import app.healthdiary.R;

public class LocationCollectionService extends Service {

    //String tag = this.toString();
    private LocationCollector locationCollector;
    private MotionDetector motionDetector;
    //private PowerManager.WakeLock mWakeLock = null;
    private Boolean b_PowerLow = false;
    public static final int SCREEN_OFF_RECEIVER_DELAY = 500;
    public static final String TAG = LocationCollectionService.class.getName();

    private final IBinder mBinder = new GPSServiceBinder();

    public LocationCollectionService() {
    }
    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive(" + intent + ")");
            final String str_Action = intent.getAction();
            Log.i(TAG, str_Action);
            if (!str_Action.equals(Intent.ACTION_SCREEN_OFF)
                    && !str_Action.equals(Intent.ACTION_BATTERY_LOW)
                    && !str_Action.equals(Intent.ACTION_BATTERY_OKAY)) {
                return;
            }

            Runnable runnable = new Runnable() {
                public void run() {
                    Log.i(TAG, "Runnable executing.");
                    if(str_Action.equals(Intent.ACTION_SCREEN_OFF) && !b_PowerLow) {
                        int count = motionDetector.getCount();
                        double AccelSum = motionDetector.getmAccelSum();
                        motionDetector.endService();
                        if (motionDetector.getB_Sensor1()) {
                            motionDetector.startManager(1);
                            motionDetector.setCount(count);
                            motionDetector.setmAccelSum(AccelSum);
                        }
                        else {
                            motionDetector.startManager(2);
                            motionDetector.setCount(count);
                            motionDetector.setmAccelSum(AccelSum);
                        }
                    }
                    if (str_Action.equals(Intent.ACTION_BATTERY_LOW))
                    {
                        Log.i(TAG, "Battery low.");
                        motionDetector.endService();
                        locationCollector.startCollectorWhenBatteryIsLow();
                        b_PowerLow = true;
                    }
                    if(str_Action.equals(Intent.ACTION_BATTERY_OKAY)){
                        Log.i(TAG, "Battery OK.");
                        locationCollector.startCollector(2);
                        locationCollector.setB_Moving(true);
                        motionDetector.startManager(2);
                        b_PowerLow = false;
                    }
                }
            };
            new Handler().postDelayed(runnable, SCREEN_OFF_RECEIVER_DELAY);
        }
    };
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate()
    {
        locationCollector = new LocationCollector(getBaseContext());
        locationCollector.setB_Moving(true);
        locationCollector.startManager(2, true,true);
        motionDetector = new MotionDetector(getBaseContext(), locationCollector);
        //the motion status is considered as moving when starting the service
        motionDetector.SwitchToSensors(2);
        //PowerManager manager =
        //        (PowerManager) getSystemService(Context.POWER_SERVICE);
        //mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_BATTERY_LOW));
        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_BATTERY_OKAY));
    }

    @Override
    public void onDestroy()
    {
        if(motionDetector != null)
            motionDetector.endService();
        if(locationCollector != null)
            locationCollector.endService();
        //if(mWakeLock != null) mWakeLock.release();
        if(mReceiver != null) unregisterReceiver(mReceiver);
        stopForeground(true);
    }

    public class GPSServiceBinder extends Binder {
        LocationCollectionService getService() {
            return LocationCollectionService.this;
         }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
        Notification noti = new Notification.Builder(getApplicationContext())
                .setContentTitle("Health Diary")
                .setContentText("Keep healthy!")
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pintent)
                .setLargeIcon(null)
                .build();
        //if startId == 0，there will be no notification in the status bar
        //mWakeLock.acquire();
        startForeground(startId, noti);
        return super.onStartCommand(intent, flags, startId);
    }
}
