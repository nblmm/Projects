package app.healthdiary.Activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Random;

import app.healthdiary.Helper.MyAlarmReceiver;
import app.healthdiary.Services.LocationCollectionService;
import app.healthdiary.R;
import app.healthdiary.SurveyHelper.SurveyAnswer;
import app.healthdiary.SurveyHelper.User;

public class SettingActivity extends Activity {
    public static final String UserInfo = "userInfo";
    private User user = User.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);

        Intent intent = getIntent();
        final SurveyAnswer surveyAnswer = (SurveyAnswer) intent.getSerializableExtra("survey");

        final TextView txt_username=(TextView) findViewById(R.id.txt_name_setting);
        txt_username.setText(surveyAnswer.getStr_UserName());

        final Button btn_Stop_Start_Location = (Button) findViewById(R.id.btn_StopStartLocationCollection_Setting);

        if(user.getN_LocationCollection() == 1)
        {
            btn_Stop_Start_Location.setText("Stop Location Collection Service");
        }
        else
            btn_Stop_Start_Location.setText("Start Location Collection Service");
        btn_Stop_Start_Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String strS = btn_Stop_Start_Location.getText().toString();
                if (strS.equals("Stop Location Collection Service")) {
                    Intent serviceintent = new Intent(SettingActivity.this, LocationCollectionService.class);
                    stopService(serviceintent);
                    btn_Stop_Start_Location.setText("Start Location Collection Service");
                    user.setN_LocationCollection(0);
                    //n_start = 0;
                    Toast.makeText(SettingActivity.this, "Location Collection Service Stopped!!!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent serviceintent = new Intent(SettingActivity.this, LocationCollectionService.class);
                    startService(serviceintent);
                    btn_Stop_Start_Location.setText("Stop Location Collection Service");
                    user.setN_LocationCollection(1);
                    //n_start = 1;
                    Toast.makeText(SettingActivity.this, "Location Collection Service Started!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Button btn_Stop_Start_Alarm = (Button) findViewById(R.id.btn_StopStartRandomMoments_Setting);
        if(user.getN_RandomGenerator() == 1)
        {
            btn_Stop_Start_Alarm.setText("Stop Generating Random Moments");
        }
        else
            btn_Stop_Start_Alarm.setText("Start Generating Random Moments");
        btn_Stop_Start_Alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String strS = btn_Stop_Start_Alarm.getText().toString();
                if (strS.equals("Stop Generating Random Moments")) {
                    setReminderForNextDay(false);
                    btn_Stop_Start_Alarm.setText("Start Generating Random Moments");
                    user.setN_RandomGenerator(0);
                    //n_start = 0;
                    Toast.makeText(SettingActivity.this, "Random Moment Generator Stopped!!!", Toast.LENGTH_SHORT).show();
                } else {
                    setReminderForNextDay(true);
                    btn_Stop_Start_Alarm.setText("Stop Generating Random Moments");
                    user.setN_RandomGenerator(1);
                    //n_start = 1;
                    Toast.makeText(SettingActivity.this, "Random Moment Generator Started!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Button btn_Home = (Button) findViewById(R.id.btn_Home_Setting);
        btn_Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // Activity
                Intent MainPageIntent = new Intent(SettingActivity.this,MainPage.class);
                //pass user info and survey infor to following activities
                MainPageIntent.putExtra("survey",surveyAnswer);

                // Use the Intent to start the MainPage Activity
                startActivity(MainPageIntent);
            }
        });

        final Button btn_StopAutoLogin = (Button) findViewById(R.id.btn_Remember_Setting);
        btn_StopAutoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SharedPreferences sp = getSharedPreferences(UserInfo, 0);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("isRemembered", false);
                editor.putString("username", "");
                editor.putString("password", "");
                // Commit the edits!
                editor.commit();

                Toast.makeText(SettingActivity.this, "You will not be logged in automatically!!!", Toast.LENGTH_SHORT).show();
            }
        });

        final Button btn_Logout = (Button) findViewById(R.id.btn_Logout_Setting);
        btn_Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SharedPreferences sp = getSharedPreferences(UserInfo, 0);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("isRemembered", false);
                // Commit the edits!
                editor.commit();

                Intent serviceintent = new Intent(SettingActivity.this, LocationCollectionService.class);
                stopService(serviceintent);

                setReminderForNextDay(false);

                Intent newLogin = new Intent(SettingActivity.this,LoginScreen.class);
                //pass user info and survey infor to following activities

                // Use the Intent to start the MainPage Activity
                startActivity(newLogin);
                Toast.makeText(SettingActivity.this, "You are logged out!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void setReminderForNextDay(boolean b) {

        // get the AlarmManager instance
        AlarmManager am= (AlarmManager) getSystemService(ALARM_SERVICE);
        // create a PendingIntent that will perform a broadcast
        PendingIntent pi= PendingIntent.getBroadcast(SettingActivity.this, 0, new Intent(SettingActivity.this, MyAlarmReceiver.class), 0);

        if(b){
            // just use current time as the Alarm time.
            Calendar c=Calendar.getInstance();
            System.out.println("Current time in millis:" + c.toString());
            //get a random moment
            Random rn = new Random(c.getTimeInMillis());
            //the random moment should be in 6:00 AM - 10:00 PM
            int interval = (int) (rn.nextDouble() * 16 * 12) * 300 * 1000;
            //the alarm will be later than 8am the next day
            c.add(Calendar.DAY_OF_YEAR,1);
            c.set(Calendar.HOUR_OF_DAY, 5);
            System.out.println("Set time:" + c.toString());

            // schedule an alarm
            am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + interval, pi);
            //am.setRepeating(AlarmManager.RTC_WAKEUP, 0,AlarmManager.INTERVAL_DAY,pi);

            c.setTimeInMillis(c.getTimeInMillis() + interval);
            System.out.println("Set interval:" + interval);
            System.out.println("Set time:" + c.toString());
        }
        else{
            // cancel current alarm
            am.cancel(pi);
        }

    }
}
