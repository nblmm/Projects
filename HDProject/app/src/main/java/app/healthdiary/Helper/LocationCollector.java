package app.healthdiary.Helper;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Created by Hongyuan on 1/6/2016.
 */
public class LocationCollector {
    private Context context;

    private static final long minTime = 1000 * 10;

    private static final long minTime_Staying = 1000 * 60 * 5;

    private static final float minDistance = 20;

    private final DateFormat timestampFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    //private StringBuffer strBufferPassive;
    //private StringBuffer strBufferGPS;
    //private StringBuffer strBufferNetwork;
    private StringBuffer strBufferLocation;

    //String tag = this.toString();

    private LocationManager locationManager_Passive;
    private LocationManager locationManager_GPS;
    private LocationManager locationManager_Network;
    private LocationListener locationListener_Passive;
    private LocationListener locationListener_GPS;
    private LocationListener locationListener_Network;
    //private GpsStatus.Listener gpsStatusListener;

    private int count = 0;
    private int n_GPSPoor = 0;
    private Boolean b_GPSPoor = false;

    public void setB_Moving(Boolean b_Moving) {
        this.b_Moving = b_Moving;
    }

    private Boolean b_Moving = true;

    public LocationCollector(Context context) {
        this.context = context;
    }
        /*
        * @para int type type of sensor, 1. Passive
        *                                2. GPS
        *                                3. Network
        */
    public void startCollector(int type)
    {
        startManager(type, b_Moving, true);
    }

    //When battery is low, switch to network positioning
    public void startCollectorWhenBatteryIsLow()
    {
        startManager(3, false, true);
    }
    /*
            * @para int nType type of sensor, 1. Passive
            *                                 2. GPS
            *                                 3. Network
            *                                 4. GPS + Network
            * @para Boolean Moving,  true: sampling frequency using per 20 meter and longer than 1 second
            *                        false:. per 5 minutes
            */
    public void startManager(int nType, Boolean Moving, Boolean endServiceFirst){
        if(endServiceFirst)
            endService();
        strBufferLocation = new StringBuffer();
        //passive
        if(nType==1){
            //strBufferPassive = new StringBuffer();
            locationManager_Passive = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

            if(locationManager_Passive.isProviderEnabled(LocationManager.PASSIVE_PROVIDER))
            {
                Location init_location = locationManager_Passive.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                if(init_location!=null) {
                    count++;
                    AppendAndSaveString("Passive",init_location);
                }
                locationListener_Passive = new LocationListener() {
                    public void onLocationChanged(Location location) {
                        count++;
                        AppendAndSaveString("Passive",location);
                    }

                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    public void onProviderEnabled(String s) {

                    }

                    public void onProviderDisabled(String s) {

                    }
                };
                locationManager_Passive.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, minTime, minDistance, locationListener_Passive);
            }
        }
        //GPS
        else if(nType==2){
            //strBufferGPS = new StringBuffer();
            locationManager_GPS = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
            if(locationManager_GPS.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                locationListener_GPS = new LocationListener() {
                    public void onLocationChanged(Location location) {
                        Log.i(this.toString(), "accuracy is: " + location.getAccuracy());
                        count++;
                        System.out.println("GPS, Lat:"+location.getLatitude()+" ;Lon:"+location.getLongitude()+"; speed: "+ location.getSpeed() +"; GPS locationChange!!!");
                        AppendAndSaveString("GPS",location);
                        //if GPS quality recovers, switch back to GPS
                        if(location.getAccuracy() > 200.0f){
                            n_GPSPoor ++;
                        }
                        else {
                            b_GPSPoor = false;
                        }
                        //if GPS quality is poor, use Network and low frequency GPS
                        if(n_GPSPoor >= 30) {
                            if (b_GPSPoor) {
                                startManager(4, b_Moving, true);
                                b_GPSPoor = true;
                            }
                            else{
                                n_GPSPoor = 0;
                                startManager(2, b_Moving, true);
                            }
                        }
                    }

                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    public void onProviderEnabled(String s) {

                    }

                    public void onProviderDisabled(String s) {

                    }
                };
                /*gpsStatusListener = new GpsStatus.Listener(){
                    public void onGpsStatusChanged(int event) {
                        GpsStatus gpsStatus = locationManager_GPS.getGpsStatus(null);
                        String strGpsStats = "";
                        int n_satellites = 0;
                        int satellitesInFix = 0;
                        int timetofix = locationManager_GPS.getGpsStatus(null).getTimeToFirstFix();
                        //Log.i(this.toString(), "Time to first fix = " + timetofix);
                        if(gpsStatus != null) {
                            Iterable<GpsSatellite>satellites = gpsStatus.getSatellites();
                            Iterator<GpsSatellite> sat = satellites.iterator();
                            int i=0;
                            while (sat.hasNext()) {
                                GpsSatellite satellite = sat.next();
                                strGpsStats+= (i++) + ": " + satellite.getPrn() + "," + satellite.usedInFix() + "," + satellite.getSnr() + "," + satellite.getAzimuth() + "," + satellite.getElevation()+ "\n\n";
                                if(satellite.usedInFix()) {
                                    satellitesInFix++;
                                }
                                n_satellites++;
                            }
                            //determine if the GPS signal is poor
                            if(satellitesInFix < 4) {
                                n_GPSSearch++;
                            }
                            else {
                                n_GPSSearch = 0;
                            }
                            if( n_GPSSearch >= 300 )
                            {
                                b_GPSPoor = true;
                                //startCollectorWhenGPSIsPoor();
                            }
                            else
                                b_GPSPoor = false;
                            //Log.i(this.toString(), "Number of satellites: " + n_satellites + "; Used In Last Fix (" + satellitesInFix + ")");
                            //System.out.println("Message: " + strGpsStats);
                        }
                    }
                };*/
                //locationManager_GPS.addGpsStatusListener(gpsStatusListener);
                if(Moving)
                    locationManager_GPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener_GPS);
                else
                    locationManager_GPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime_Staying, 0, locationListener_GPS);
            }
        }
        //network
        else if(nType == 3) {
            //strBufferNetwork = new StringBuffer();
            locationManager_Network = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager_Network.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationListener_Network = new LocationListener() {
                    public void onLocationChanged(Location location) {
                        count++;
                        System.out.println("Network, Lat:" + location.getLatitude() + " ;Lon:" + location.getLongitude() + "; speed: " + location.getSpeed() + ";Network locationChange!!!");
                        AppendAndSaveString("Network",location);
                    }

                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    public void onProviderEnabled(String s) {

                    }

                    public void onProviderDisabled(String s) {

                    }
                };
                if(Moving)
                    locationManager_Network.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, locationListener_Network);
                else
                    locationManager_Network.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime_Staying, 0, locationListener_Network);
            }
            else if (nType == 4){
                startManager(2,false,true);
                startManager(3,b_Moving, false);
            }
        }
    }
    //Append new location information to the existing data string.
    //If 50 location points are appended, Append the string to the local file.
    //@para String locationSource: GPS, Network or Passive
    //@para Location location: new location information
    private void AppendAndSaveString(String locationSource, Location location){
        GregorianCalendar greg = new GregorianCalendar();
        strBufferLocation.append(System.getProperty("line.separator"));
        strBufferLocation.append(locationSource);
        strBufferLocation.append(", ");
        strBufferLocation.append(location.getLatitude());
        strBufferLocation.append(", ");
        strBufferLocation.append(location.getLongitude());
        strBufferLocation.append(", ");
        strBufferLocation.append(timestampFormat.format(greg.getTime()));
        strBufferLocation.append(", ");
        strBufferLocation.append(location.getSpeed());
        strBufferLocation.append(", ");
        strBufferLocation.append(location.getAccuracy());

        //store the data
        if (count >= 50) {
            int s = SavetoFile("Location.txt", strBufferLocation);
            if (s == 1) {
                //strBufferNetwork.delete(0, strBufferNetwork.length());
                System.out.println("Location_Network Saved");
            } else
                System.out.println("Location_Network Failed");
            count = 0;
        }
    }

    public void endService()
    {
        if(strBufferLocation != null && SavetoFile("Location.txt",strBufferLocation)==1) {
            System.out.println("Location data Saved");
            count = 0;
        }

        if(locationManager_Network != null && locationListener_Network != null)
        {
            locationManager_Network.removeUpdates(locationListener_Network);
        }
        if(locationManager_Passive != null && locationListener_Passive != null)
        {
            locationManager_Passive.removeUpdates(locationListener_Passive);
        }
        if(locationManager_GPS != null && locationListener_GPS != null)
        {
            locationManager_GPS.removeUpdates(locationListener_GPS);
            //locationManager_GPS.removeGpsStatusListener(gpsStatusListener);
        }
    }

    private int SavetoFile(String FileName, StringBuffer strBuffer){
        if(strBuffer == null)
            return 0;
        File strDir;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            strDir = Environment.getExternalStorageDirectory();//get SDCard path
        }
        else
        {
            strDir = Environment.getDataDirectory();//get local path
        }
        File saveFile = new File(strDir, FileName);
        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(saveFile, true);
            if(!saveFile.exists()) {
                outStream.write("Source, Latitude, Longitude, Time, Speed".getBytes());
            }
            OutputStreamWriter wt = new OutputStreamWriter(outStream);
            wt.append(strBuffer.toString());
            wt.close();
            outStream.close();
            //clear the buffer
            strBuffer.delete(0, strBuffer.length());
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }
}
