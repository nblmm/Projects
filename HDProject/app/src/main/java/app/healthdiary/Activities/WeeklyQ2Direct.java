package app.healthdiary.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import app.healthdiary.R;
import app.healthdiary.SurveyHelper.SavedAnswerOperations;
import app.healthdiary.SurveyHelper.SurveyAnswer;


public class WeeklyQ2Direct extends Activity {
    final Context context = this;
    protected static final String TAG = "MainActivity";
    private SeekBar seekBar_weeklyq2;
    private TextView txt_wq2;
    private SurveyAnswer surveyAnswer;
    private SavedAnswerOperations SAO;
    //private SeekBar seekBar_weeklyq3;
    //private TextView txt_wq3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_weekly_q2_direct);

        //get info from the last activity
        Intent intent = getIntent();
        surveyAnswer = (SurveyAnswer) intent.getSerializableExtra("surveyanswer");
        final String flag = intent.getStringExtra("flag");
        TextView txt_period = (TextView) findViewById(R.id.txt_periodq2);
        //System.out.println(surveyAnswer.getStr_StartTime());
        if(surveyAnswer.getStr_StartTime().length()>10 && surveyAnswer.getStr_EndTime().length()>10)
            txt_period.setText(surveyAnswer.getStr_StartTime().substring(0,10)+" - "+surveyAnswer.getStr_EndTime().substring(0,10));
        else
            txt_period.setText(surveyAnswer.getStr_StartTime() + " - " + surveyAnswer.getStr_EndTime());

        System.out.println(surveyAnswer.getStr_UserName()+"++++++++++++");
        txt_wq2 = (TextView) findViewById(R.id.txt_wq2);
        seekBar_weeklyq2= (SeekBar) findViewById(R.id.seekBar_weeklyq2);
        seekBar_weeklyq2.setOnSeekBarChangeListener(seekListener);

        //txt_wq3 = (TextView) findViewById(R.id.txt_wq3);
        //seekBar_weeklyq3= (SeekBar) findViewById(R.id.seekBar_weeklyq3);
        //seekBar_weeklyq3.setOnSeekBarChangeListener(seekListener1);


        Button btn_next=(Button)findViewById(R.id.btn_q2next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                surveyAnswer.setN_SymptomsinFamily(Integer.parseInt(txt_wq2.getText().toString()));
                if(null==flag || flag.equals("new")) {
                    Intent Q3Intent = new Intent(WeeklyQ2Direct.this,
                            WeeklyQ3Direct.class);
                    Q3Intent.putExtra("surveyanswer", surveyAnswer);
                    Q3Intent.putExtra("flag","new");
                    // Use the Intent to start the MainPage Activity
                    startActivity(Q3Intent);
                }
                /*else if(flag.equals("update")){
                    Intent intent = new Intent(WeeklyQ2Direct.this, WeeklyQuestions.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("surveyanswer", surveyAnswer);
                    startActivity(intent);
                }
                */
                SAO = SavedAnswerOperations.getInstance();
                SharedPreferences sp = context.getSharedPreferences(SAO.getFileName(), 0);
                SAO.setSp(sp);
                SAO.UpdateAnswers(1, new int []{Integer.parseInt(txt_wq2.getText().toString())});
            }
        });
        Button btn_q2cancel=(Button)findViewById(R.id.btn_q2cancel);
        btn_q2cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(null==flag || flag.equals("new")){
                    Intent intent = new Intent(WeeklyQ2Direct.this, MainPage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("survey", surveyAnswer);
                    startActivity(intent);
                }
                /*else if (flag.equals("update")){
                    Intent intent = new Intent(WeeklyQ2Direct.this, WeeklyQuestions.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("surveyanswer", surveyAnswer);
                    startActivity(intent);
                }
                */
            }
        });
    }

    private OnSeekBarChangeListener seekListener = new OnSeekBarChangeListener(){
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
                             txt_wq2.setText(Integer.toString(progress));

                     }
             };
/*
    private OnSeekBarChangeListener seekListener1 = new OnSeekBarChangeListener(){
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
            txt_wq3.setText(Integer.toString(progress));

        }
    };
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weekly_q2_direct, menu);
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
}
