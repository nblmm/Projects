package app.healthdiary.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

import java.util.ArrayList;
import java.util.List;

import app.healthdiary.R;
import app.healthdiary.SurveyHelper.SavedAnswerOperations;
import app.healthdiary.SurveyHelper.Option;
import app.healthdiary.SurveyHelper.Question;
import app.healthdiary.SurveyHelper.Survey;
import app.healthdiary.SurveyHelper.SurveyAnswer;
import app.healthdiary.SurveyHelper.SurveyHeading;
import app.healthdiary.SurveyHelper.User;


public class WeeklyQ3Direct extends Activity {
    final Context context = this;
    private boolean[] selection;
    private ListView lv;
    private SurveyAnswer surveyAnswer;
    private SavedAnswerOperations SAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_weekly_q3_direct);

        Intent intent = getIntent();
        surveyAnswer = (SurveyAnswer) intent.getSerializableExtra("surveyanswer");
        //System.out.println(surveyAnswer.getStr_UserName()+"3333333333");
        final String flag = intent.getStringExtra("flag");

        TextView txt_period = (TextView) findViewById(R.id.txt_periodq3);
        if(surveyAnswer.getStr_StartTime().length()>10 && surveyAnswer.getStr_EndTime().length()>10)
            txt_period.setText(surveyAnswer.getStr_StartTime().substring(0,10)+" - "+surveyAnswer.getStr_EndTime().substring(0,10));
        else
            txt_period.setText(surveyAnswer.getStr_StartTime() + " - " + surveyAnswer.getStr_EndTime());

        final String [] str_Contacts={
                "None",
                "Less than 5",
                "6 to 10",
                "11 to 20",
                "More than 20"};
        selection=new boolean[6];
        lv = (ListView) findViewById(R.id.lv_dq3);//ListView
        /*Create Adapter to bind data for the Listview*/
        lv.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, str_Contacts));
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        Button btn_next=(Button)findViewById(R.id.btn_q3next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ArrayAdapter myadapter = (ArrayAdapter) lv.getAdapter();
                SparseBooleanArray checked = lv.getCheckedItemPositions();
                //ArrayList<String> selectedItems = new ArrayList<String>();
                //StringBuffer sb = new StringBuffer(5);
                int catContact = -1;
                for (int i = 0; i < checked.size(); i++) {
                    // Item position in adapter
                    int position = checked.keyAt(i);
                    // Add sport if it is checked i.e.) == TRUE!
                    if (checked.valueAt(i)) {
                        //selectedItems.add((String) myadapter.getItem(position));
                        //sb.append((String) myadapter.getItem(position) + ", ");
                        catContact = position;
                    }
                }
                //String contacts = sb.toString();
                //if (contacts.length() >= 2)
                //    contacts = contacts.subSequence(0, contacts.length() - 2).toString();
                //Toast.makeText(WeeklyQ1Direct.this, "Symptoms:" + symptoms, Toast.LENGTH_SHORT).show();
                System.out.println("Category:" + catContact);
                surveyAnswer.setN_SymptomsNonFamilyCategory(catContact);
                if(catContact==-1){
                    Toast.makeText(WeeklyQ3Direct.this, "Please check an item", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (null==flag || flag.equals("new")) {
                        /*Intent Q4Intent = new Intent(WeeklyQ3Direct.this,
                                WeeklyQ4Direct.class);
                        //pass the question answer to the next activity
                        Q4Intent.putExtra("surveyanswer", surveyAnswer);
                        Q4Intent.putExtra("flag", "new");
                        // Use the Intent to start the MainPage Activity
                        startActivity(Q4Intent);*/

                        /*new HttpGetTask().execute();
                        Intent intent = new Intent(WeeklyQ3Direct.this, MainPage.class);
                        SurveyAnswer newSurvey = new SurveyAnswer(surveyAnswer.getStr_UserName(), surveyAnswer.getStr_Password(), surveyAnswer.getStr_SessionID(), surveyAnswer.getStr_StartTime(), surveyAnswer.getStr_EndTime());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("survey", newSurvey);
                        startActivity(intent);*/
                        SAO = SavedAnswerOperations.getInstance();
                        SharedPreferences sp = context.getSharedPreferences(SAO.getFileName(), 0);
                        SAO.setSp(sp);
                        int []b = {0,0,0,0,0};
                        b[surveyAnswer.getN_SymptomsNonFamilyCategory()] = 1;
                        SAO.UpdateAnswers(2, b);
                        new HttpGetTask().execute();
                        Intent RandomMomentIntent = new Intent(WeeklyQ3Direct.this,
                                RandomMomentContacts.class);
                        RandomMomentIntent.putExtra("surveyanswer",surveyAnswer);
                        startActivity(RandomMomentIntent);
                    }
                    /*else if (flag.equals("update")) {
                        Intent intent = new Intent(WeeklyQ3Direct.this, WeeklyQuestions.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("surveyanswer", surveyAnswer);
                        startActivity(intent);

                        Intent intent = new Intent(WeeklyQ3Direct.this, WeeklyQuestions.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("surveyanswer", surveyAnswer);
                        startActivity(intent);
                    }
                    */
                }

            }
        });
        Button btn_q1cancel=(Button)findViewById(R.id.btn_q3cancel);
        btn_q1cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(null==flag || flag.equals("new")) {
                    Intent intent = new Intent(WeeklyQ3Direct.this, MainPage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("survey", surveyAnswer);
                    startActivity(intent);
                }
                /*else if(flag.equals("update"))
                {
                    Intent intent = new Intent(WeeklyQ3Direct.this, WeeklyQuestions.class);
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
        getMenuInflater().inflate(R.menu.menu_weekly_q3_direct, menu);
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
            if(!user.isNetworkAvailable(WeeklyQ3Direct.this.getApplicationContext())){
                ls.add("no connection");
                return ls;
            }
            int[] bsymptoms = surveyAnswer.getbSympton();
            if(user.isSignedIn){
                //Build up a survey
                //Step 1:  Start with Option, then with Questions, lastly, add question to survey;
                Option op11 = new Option(601, bsymptoms[0],101);
                Option op12 = new Option(602, bsymptoms[1],101);
                Option op13 = new Option(603, bsymptoms[2],101);
                Option op14 = new Option(604, bsymptoms[3],101);
                Option op15 = new Option(605, bsymptoms[4],101);
                Option op16 = new Option(600, bsymptoms[5],101);

                Question q1 = new Question(900);;
                q1.addOption(op11);
                q1.addOption(op12);
                q1.addOption(op13);
                q1.addOption(op14);
                q1.addOption(op15);
                q1.addOption(op16);

                Option op2 = new Option(501, surveyAnswer.getN_SymptomsinFamily(),102);
                int []b = {0,0,0,0,0};
                b[surveyAnswer.getN_SymptomsNonFamilyCategory()] = 1;
                Option op31 = new Option(400, b[0], 101);
                Option op32 = new Option(401, b[1], 101);
                Option op33 = new Option(402, b[2], 101);
                Option op34 = new Option(403, b[3], 101);
                Option op35 = new Option(404, b[4], 101);

                Question q2 = new Question(901);
                q2.addOption(op2);
                Question q3 = new Question(902);
                q3.addOption(op31);
                q3.addOption(op32);
                q3.addOption(op33);
                q3.addOption(op34);
                q3.addOption(op35);

                Survey survey = new Survey();
                survey.addQuestion(q1);
                survey.addQuestion(q2);
                survey.addQuestion(q3);
                //survey.addQuestion(q4);

                SurveyHeading head = new SurveyHeading(surveyAnswer.getStr_StartTime(), surveyAnswer.getStr_EndTime(), 100);
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
                Toast.makeText(WeeklyQ3Direct.this, "Submission fail, please try again!", Toast.LENGTH_SHORT).show();
            }
            else if (result.get(0).toString().equals("no connection"))
            {
                Toast.makeText(WeeklyQ3Direct.this, "No network connection.Please check your network and try later!" , Toast.LENGTH_SHORT).show();
            }
            else {
                //Toast.makeText(WeeklyQ3Direct.this, "Your survey has been submitted!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
