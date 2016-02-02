package app.healthdiary.Activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.os.AsyncTask;

import app.healthdiary.Services.LocationCollectionService;
import app.healthdiary.Helper.MyAlarmReceiver;
import app.healthdiary.R;
import app.healthdiary.SurveyHelper.SavedAnswerOperations;
import app.healthdiary.SurveyHelper.SurveyAnswer;
import app.healthdiary.SurveyHelper.User;

public class LoginScreen extends Activity {

    private EditText uname;
    private EditText passwd;
    private String str_Uname="";
    private String str_Passwd="";
    private CheckBox cb_RemeberMe;
    public static final String UserInfo = "userInfo";
    public static final String SavedAnswers = "savedAnswers";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loginscreen);

        cb_RemeberMe = (CheckBox) findViewById(R.id.cb_RememberMe);
        SharedPreferences sp = this.getSharedPreferences(UserInfo, 0);
        SavedAnswerOperations SAO = SavedAnswerOperations.getInstance();
        SAO.setFileInfo(SavedAnswers);
        Boolean is_Remember = sp.getBoolean("isRemembered", false);
        uname = (EditText) findViewById(R.id.username_edittext);
        passwd = (EditText) findViewById(R.id.password_edittext);
        if(is_Remember){
            cb_RemeberMe.setChecked(true);
            uname.setText(sp.getString("username", ""));
            passwd.setText(sp.getString("password", ""));
            Toast.makeText(LoginScreen.this, "Logging in as " + sp.getString("username", "")+". Please hold on a second!", Toast.LENGTH_SHORT).show();
            new HttpGetTask().execute();
        }
        str_Uname = uname.getText().toString();
        str_Passwd = passwd.getText().toString();
        final Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                new HttpGetTask().execute();
                if(cb_RemeberMe.isChecked()){
                    SharedPreferences sp = getSharedPreferences(UserInfo, 0);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("isRemembered",true);
                    editor.putString("username", uname.getText().toString());
                    editor.putString("password",passwd.getText().toString());
                    // Commit the edits!
                    editor.apply();
                }
            }
        });
    }
    //Async thread to connect the Internet
    private class HttpGetTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... params) {
            String name = str_Uname;
            String pass = str_Passwd;
            List<String> ls=new ArrayList<String>();
            User user = User.getInstance();
            user.setUserInfo(name, pass);
            if(!User.isNetworkAvailable(LoginScreen.this.getApplicationContext())){
                ls.add("no connection");
                return ls;
            }
            //user.signup();
            user.signin();

            if(user.isSignedIn){
                Calendar c=Calendar.getInstance();
                c.setTime(new Date());
                c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                String WeekStartDate = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
                c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                String WeekEndDate = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
                //for new surveys
                SurveyAnswer survey = new SurveyAnswer(name, pass, user.getSessionID(), WeekStartDate + " 00:00:00", WeekEndDate + " 11:59:59");
                //SurveyAnswer survey = new SurveyAnswer(name, pass, user.getSessionID(), "2015-10-01 00:00:00", "2015-10-07 11:59:59");
                System.out.println("week date :" + WeekStartDate + " 00:00:00    " + WeekEndDate + " 11:59:59");
                Intent ServiceIntent = new Intent(getBaseContext(), LocationCollectionService.class);
                startService(ServiceIntent);

                user.setN_LocationCollection(1);
                //set random moments
                setReminder(false);
                setReminder(true);
                user.setN_RandomGenerator(1);
                //SurveyAnswer survey = new SurveyAnswer();
                // Create an explicit Intent for starting the MainPage
                // Activity
                Intent MainPageIntent = new Intent(LoginScreen.this,MainPage.class);
                //pass user info and survey infor to following activities
                MainPageIntent.putExtra("survey",survey);

                // Use the Intent to start the MainPage Activity
                startActivity(MainPageIntent);


                ls.add("success");
            }
            else
            {
                ls.add("fail");
            }
            return ls;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            //Toast.makeText(LoginScreen.this, result.get(0).toString(), Toast.LENGTH_SHORT).show();
            if(result.get(0).equals("fail")){
                Toast.makeText(LoginScreen.this, "Username and password do not match, please try again", Toast.LENGTH_SHORT).show();
                uname.setText("");
                passwd.setText("");
            }
            else if (result.get(0).equals("no connection"))
            {
                Toast.makeText(LoginScreen.this, "No network connection.Please check your network and try later!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(LoginScreen.this, "Welcome to Health Diary! " + uname.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        }

        private void setReminder(boolean b) {

            // get the AlarmManager instance
            AlarmManager am= (AlarmManager) getSystemService(ALARM_SERVICE);
            // create a PendingIntent that will perform a broadcast
            PendingIntent pi= PendingIntent.getBroadcast(LoginScreen.this, 0, new Intent(LoginScreen.this, MyAlarmReceiver.class), 0);

            if(b){
                // just use current time as the Alarm time.
                Calendar c=Calendar.getInstance();
                long now = c.getTimeInMillis();
                System.out.println("Current time in millis:" + c.getTimeInMillis());
                //get a random moment
                Random rn = new Random(c.getTimeInMillis());
                //the random moment should be in 6:00 AM - 10:00 PM
                int interval = (int) (rn.nextDouble() * 16 * 12) * 300 * 1000;
                //the alarm will be later than 8am
                c.set(Calendar.HOUR_OF_DAY, 5);
                System.out.println("Set time in millis:" + c.getTimeInMillis());
                // schedule an alarm
                // if the random moment is the past
                if(now >= c.getTimeInMillis() + interval)
                {
                    am.set(AlarmManager.RTC_WAKEUP, now + 10 * 1000, pi);
                }
                else {
                    am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + interval, pi);
                }
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

}
