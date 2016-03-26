package app.healthdiary.Activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import app.healthdiary.Helper.MyAlarmReceiver;
import app.healthdiary.R;
import app.healthdiary.Services.LocationCollectionService;
import app.healthdiary.SurveyHelper.SavedAnswerOperations;
import app.healthdiary.SurveyHelper.SurveyAnswer;
import app.healthdiary.SurveyHelper.User;


public class WeeklyQ1Direct extends Activity {
    public static final int NOTIFICATION_ID = 1;
    private boolean[] selection;
    private ListView lv;
    private SurveyAnswer surveyAnswer;
    private TextView txt_period;
    private SharedPreferences sp;
    private SavedAnswerOperations SAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_weekly_q1_direct);

        txt_period = (TextView) findViewById(R.id.txt_periodq1);
        //get info from the last activity
        Intent intent = getIntent();
        //determine if to trigger the alarm or not
        int n_notify = 0;
        n_notify = intent.getIntExtra("notify", n_notify);
        final NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        SAO = SavedAnswerOperations.getInstance();
        sp = this.getSharedPreferences(SAO.getFileName(), 0);
        //restart the location collection service to make sure the current location is caught
        Intent ServiceIntent = new Intent(getBaseContext(), LocationCollectionService.class);
        startService(ServiceIntent);
        //a new random moment
        if(n_notify == 1) {
            //if there are data that is not submitted, submit the data here
            if(sp.getBoolean("isSaved",false) && sp.getInt("LastSavedQuestion",2) < 2){
                SAO.setSp(sp);
                if(!SAO.Submit(this)){
                    Toast.makeText(WeeklyQ1Direct.this, "Your last unsaved answer is lost!", Toast.LENGTH_SHORT).show();
                }
            }
            // create the instance of NotificationManager
            // create the instance of Notification
            Notification n = new Notification();

            //n.sound=Uri.parse("file:///sdcard/alarm.mp3");
            //n.icon = R.drawable.ic_launcher;
            n.tickerText = "It's time to do a quiz!";
            n.defaults = Notification.DEFAULT_SOUND;
            //n.sound=Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "20");
            // Post a notification to be shown in the status bar
            nm.notify(NOTIFICATION_ID, n);
            setReminderForNextDay(false);
            setReminderForNextDay(true);
        }
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
        final String flag = intent.getStringExtra("flag");
        //resume the previous answer
        if(sp.getBoolean("isSaved",false)){
            int index =  sp.getInt("LastSavedQuestion", 0);
            int [] isSym = {0,0,0,0,0,0};
            switch (index){
                case 0:
                    Intent intent1 = new Intent(WeeklyQ1Direct.this, WeeklyQ2Direct.class);
                    isSym[0] = sp.getInt("601",0);
                    isSym[1] = sp.getInt("602",0);
                    isSym[2] = sp.getInt("603",0);
                    isSym[3] = sp.getInt("604",0);
                    isSym[4] = sp.getInt("605",0);
                    isSym[5] = sp.getInt("600",0);
                    surveyAnswer.setbSympton(isSym);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent1.putExtra("surveyanswer", surveyAnswer);
                    startActivity(intent1);
                    break;
                case 1:
                    Intent intent2 = new Intent(WeeklyQ1Direct.this, WeeklyQ3Direct.class);
                    isSym[0] = sp.getInt("601",0);
                    isSym[1] = sp.getInt("602",0);
                    isSym[2] = sp.getInt("603",0);
                    isSym[3] = sp.getInt("604",0);
                    isSym[4] = sp.getInt("605",0);
                    isSym[5] = sp.getInt("600",0);
                    surveyAnswer.setbSympton(isSym);
                    surveyAnswer.setN_SymptomsinFamily(sp.getInt("501", 0));
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent2.putExtra("surveyanswer", surveyAnswer);
                    startActivity(intent2);
                    break;
                case 2:
                    Intent intent3 = new Intent(WeeklyQ1Direct.this, RandomMomentContacts.class);
                    intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent3.putExtra("surveyanswer", surveyAnswer);
                    startActivity(intent3);
                    break;
            }
            Toast.makeText(WeeklyQ1Direct.this, "Your last survey resumed!", Toast.LENGTH_SHORT).show();
        }

        /////////////////////////////////////////////
        /*
        if(surveyAnswer.getStr_StartTime().length()>10 && surveyAnswer.getStr_EndTime().length()>10)
            txt_period.setText(surveyAnswer.getStr_StartTime().substring(0,10)+" - "+surveyAnswer.getStr_EndTime().substring(0,10));
        else
            txt_period.setText(surveyAnswer.getStr_StartTime() + " - " + surveyAnswer.getStr_EndTime());
            */
        /////////////////////////////////////////////
        final String [] str_Symptoms={
                "Fever",
                "Runny nose/congestion/sneezing",
                "Coughing",
                "Sore throat",
                "Nausea/vomiting/diarrhea",
                "None of the above"};
        selection=new boolean[6];
        lv = (ListView) findViewById(R.id.lv_dq1symptoms);//ListView
        /*Create Adapter to bind data for the Listview*/
        lv.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, str_Symptoms));
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        Button btn_next=(Button)findViewById(R.id.btn_q1next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                nm.cancel(NOTIFICATION_ID);
                ArrayAdapter myadapter = (ArrayAdapter) lv.getAdapter();
                SparseBooleanArray checked = lv.getCheckedItemPositions();
                //ArrayList<String> selectedItems = new ArrayList<String>();
                StringBuffer sb = new StringBuffer(6);
                int isSym[] = {0, 0, 0, 0, 0, 0};
                for (int i = 0; i < checked.size(); i++) {
                    // Item position in adapter
                    int position = checked.keyAt(i);
                    // Add sport if it is checked i.e.) == TRUE!
                    if (checked.valueAt(i)) {
                        //selectedItems.add((String) myadapter.getItem(position));
                        sb.append((String) myadapter.getItem(position) + ", ");
                        isSym[position] = 1;
                    }
                }
                String symptoms = sb.toString();
                if (symptoms.length() >= 2)
                    symptoms = symptoms.subSequence(0, symptoms.length() - 2).toString();
                surveyAnswer.setStr_SymptomDescription(symptoms);
                surveyAnswer.setbSympton(isSym);
                SAO.setStartTime(surveyAnswer.getStr_StartTime());
                SAO.setEndTime(surveyAnswer.getStr_EndTime());
                SAO.setSp(sp);
                SAO.UpdateAnswers(0, isSym);
                System.out.println(isSym[0]+" "+isSym[1]+" "+isSym[2]+" "+isSym[3]+" "+isSym[4]+" "+isSym[5]);
                if((isSym[0]==1||isSym[1]==1||isSym[2]==1||isSym[3]==1||isSym[4]==1)&& isSym[5]==1)
                {
                    Toast.makeText(WeeklyQ1Direct.this, "Do not choose any symptom if you check None of the above", Toast.LENGTH_SHORT).show();
                }
                else if(isSym[5]==0 && isSym[0]==0&&isSym[1]==0&&isSym[2]==0&&isSym[3]==0&&isSym[4]==0)
                {
                    Toast.makeText(WeeklyQ1Direct.this, "Please check at least one item", Toast.LENGTH_SHORT).show();
                }
                //new data entry
                else {
                    if (null==flag || flag.equals("new")) {
                        //Toast.makeText(WeeklyQ1Direct.this, "Symptoms:" + symptoms, Toast.LENGTH_SHORT).show();
                        Intent Q2Intent = new Intent(WeeklyQ1Direct.this,
                                WeeklyQ2Direct.class);
                        //pass the question answer to the next activity
                        Q2Intent.putExtra("surveyanswer", surveyAnswer);
                        Q2Intent.putExtra("flag", "new");
                        // Use the Intent to start the MainPage Activity
                        startActivity(Q2Intent);
                    }
                    /*else if (flag.equals("update"))//update
                    {
                        Intent intent = new Intent(WeeklyQ1Direct.this, WeeklyQuestions.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("surveyanswer", surveyAnswer);
                        startActivity(intent);
                    }*/
                }

            }
        });
        Button btn_q1cancel=(Button)findViewById(R.id.btn_q1cancel);
        btn_q1cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                nm.cancel(NOTIFICATION_ID);
                if(null==flag || flag.equals("new")) {
                    Intent intent = new Intent(WeeklyQ1Direct.this, MainPage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("survey", surveyAnswer);
                    startActivity(intent);
                }
                /*else if (flag.equals("update")){
                    Intent intent = new Intent(WeeklyQ1Direct.this, WeeklyQuestions.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("surveyanswer", surveyAnswer);
                    startActivity(intent);
                }
                */
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weekly_q1_direct, menu);
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
        PendingIntent pi= PendingIntent.getBroadcast(WeeklyQ1Direct.this, 0, new Intent(WeeklyQ1Direct.this, MyAlarmReceiver.class), 0);

        if(b){
            // just use current time as the Alarm time.
            Calendar c=Calendar.getInstance();
            System.out.println("Current time in millis:" + c.toString());
            //get a random moment
            Random rn = new Random(c.getTimeInMillis());
            //the random moment should be in 6:00 AM - 10:00 PM
            int interval = (int) (rn.nextDouble() * 16 * 12) * 300 * 1000;
            //the alarm will be later than 8am the next day
            c.add(Calendar.DAY_OF_YEAR, 1);
            c.set(Calendar.HOUR_OF_DAY, 5);
            c.set(Calendar.MINUTE, 0);
            System.out.println("Set time:" + c.toString());
            // schedule an alarm
            am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + interval, pi);
            //am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + 600000, pi);
            //am.setRepeating(AlarmManager.RTC_WAKEUP, 0,AlarmManager.INTERVAL_DAY,pi);

            c.setTimeInMillis(c.getTimeInMillis() + interval);
            txt_period.setText(c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
            System.out.println("Set interval:" + interval);
            System.out.println("Set time:" + c.toString());
            System.out.println("/////////////////////////////////////////////");
        }
        else{
            // cancel current alarm
            am.cancel(pi);
        }
    }
}
