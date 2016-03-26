package app.healthdiary.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import app.healthdiary.R;
import app.healthdiary.SurveyHelper.SavedAnswerOperations;
import app.healthdiary.SurveyHelper.Option;
import app.healthdiary.SurveyHelper.Question;
import app.healthdiary.SurveyHelper.Survey;
import app.healthdiary.SurveyHelper.SurveyAnswer;
import app.healthdiary.SurveyHelper.SurveyHeading;
import app.healthdiary.SurveyHelper.User;


public class RandomMomentContacts extends Activity {
    final Context context = this;
    private SeekBar seekBar_randomq2;
    private TextView txt_wrq2;
    private SurveyAnswer surveyAnswer;
    private SurveyAnswer surveyAnswer_Random;
    private int n_Nmuber=0;
    private SavedAnswerOperations SAO;
    //public static final int NOTIFICATION_ID = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_random_moment_contacts);

        Intent intent = getIntent();
        /*int n_notify = 0;
        n_notify = intent.getIntExtra("notify", n_notify);

        final NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(n_notify == 1) {
            // create the instance of NotificationManager
            // create the instance of Notification
            Notification n = new Notification();

            //n.sound=Uri.parse("file:///sdcard/alarm.mp3");
            //n.icon = R.drawable.ic_launcher;
            //n.tickerText = "It's time to do a quiz!";
            n.defaults = Notification.DEFAULT_SOUND;
            //n.sound=Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "20");
            // Post a notification to be shown in the status bar
            nm.notify(NOTIFICATION_ID, n);
        }*/
        //get info from the last activity
        surveyAnswer = (SurveyAnswer) intent.getSerializableExtra("surveyanswer");
        if (null == surveyAnswer)
        {
            User user = User.getInstance();
            Calendar c=Calendar.getInstance();
            c.setTime(new Date());
            c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            String WeekStartDate = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
            c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
            String WeekEndDate = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
            //for new surveys
            surveyAnswer = new SurveyAnswer(user.getMyUsername(), "", user.getSessionID(), WeekStartDate + " 00:00:00", WeekEndDate + " 11:59:59");
        }
        Calendar c=Calendar.getInstance();
        String t = c.get(Calendar.YEAR)+"-"+c.get(Calendar.MONTH)+"-"+c.get(Calendar.DAY_OF_MONTH)+" "+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+":00";
        System.out.println(t);
        //User user = User.getInstance();
        surveyAnswer_Random = new SurveyAnswer(surveyAnswer.getStr_UserName(), surveyAnswer.getStr_Password(), surveyAnswer.getStr_SessionID(), t, t);

        System.out.println(surveyAnswer.getStr_UserName() + "rrrrrrrrrr");

        txt_wrq2 = (TextView) findViewById(R.id.txt_wrq2);
        seekBar_randomq2= (SeekBar) findViewById(R.id.seekBar_randomq2);
        seekBar_randomq2.setOnSeekBarChangeListener(seekListener);
        Button btn_rqcsubmit=(Button)findViewById(R.id.btn_rqcsubmit);
        btn_rqcsubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Toast.makeText(RandomMomentContacts.this, "Your random moment survey has been submitted!", Toast.LENGTH_SHORT).show();
                //nm.cancel(NOTIFICATION_ID);
                //finish();
                n_Nmuber = Integer.parseInt(txt_wrq2.getText().toString());
                new HttpGetTask().execute();
                SAO = SavedAnswerOperations.getInstance();
                SharedPreferences sp = context.getSharedPreferences(SAO.getFileName(), 0);
                SAO.setSp(sp);
                SAO.ClearAll();
                Intent intent = new Intent(RandomMomentContacts.this, MainPage.class);
                //SurveyAnswer newSurvey = new SurveyAnswer(surveyAnswer.getStr_UserName(),surveyAnswer.getStr_Password(),surveyAnswer.getStr_SessionID(),surveyAnswer.getStr_StartTime(),surveyAnswer.getStr_EndTime());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("survey", surveyAnswer);
                startActivity(intent);
            }
        });
        Button btn_rqccancel=(Button)findViewById(R.id.btn_rqccancel);
        btn_rqccancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //nm.cancel(NOTIFICATION_ID);
                Intent intent = new Intent(RandomMomentContacts.this, MainPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("survey", surveyAnswer);
                startActivity(intent);
            }
        });
        //set the alarm for the next day
        //setReminderForNextDay(false);
        //setReminderForNextDay(true);
    }

    private SeekBar.OnSeekBarChangeListener seekListener = new SeekBar.OnSeekBarChangeListener(){
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //Log.i(TAG, "onStopTrackingTouch");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //Log.i(TAG,"onStartTrackingTouch");
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            //Log.i(TAG,"onProgressChanged");
            txt_wrq2.setText(Integer.toString(progress));

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_random_moment_contacts, menu);
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
    //Async thread to connect the Internet
    private class HttpGetTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... params) {
            List<String> ls=new ArrayList<String>();
            /*User user = new User();
            user.setUserInfo(surveyAnswer.getStr_UserName(), surveyAnswer.getStr_Password());
            user.signin();*/
            //use singleton pattern
            User user = User.getInstance();
            if(!user.isNetworkAvailable(RandomMomentContacts.this.getApplicationContext())){
                ls.add("no connection");
                return ls;
            }

            if(user.isSignedIn){
                //Build up a survey
                //Step 1:  Start with Option, then with Questions, lastly, add question to survey;
                Option opr = new Option(301, n_Nmuber,102);


                Question q1 = new Question(999);;
                q1.addOption(opr);

                Survey survey = new Survey();
                survey.addQuestion(q1);

                SurveyHeading head = new SurveyHeading(surveyAnswer_Random.getStr_StartTime(), surveyAnswer_Random.getStr_EndTime(), 200);
                survey.setHeading(head);

                user.submitSurvey(survey);
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
            if(result.get(0).toString().equals("fail")){
                Toast.makeText(RandomMomentContacts.this, "Submission fail, please try again!", Toast.LENGTH_SHORT).show();
            }
            else if (result.get(0).toString().equals("no connection"))
            {
                Toast.makeText(RandomMomentContacts.this, "No network connection.Please check your network and try later!" , Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(RandomMomentContacts.this, "Your survey has been submitted!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
