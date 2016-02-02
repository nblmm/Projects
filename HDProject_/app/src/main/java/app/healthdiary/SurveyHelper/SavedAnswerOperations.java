package app.healthdiary.SurveyHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hongyuan on 9/4/2015.
 */
public class SavedAnswerOperations {
    public SharedPreferences getSp() {
        return sp;
    }

    public void setSp(SharedPreferences sp) {
        this.sp = sp;
    }

    private SharedPreferences sp;

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    private String FileName;

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    private String StartTime;
    private String EndTime;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private Context context;

    public static SavedAnswerOperations getInstance()
    {
        if(instance == null){
            instance = new SavedAnswerOperations();
        }
        return instance;
    }

    private static SavedAnswerOperations instance = null;

    protected SavedAnswerOperations() {

    }

    public Boolean getSaved(){
        return sp.getBoolean("isSaved", false);
    }
    public void setFileInfo(final String FileName)
    {
        this.FileName = FileName;
    }
    public SavedAnswerOperations(SharedPreferences sp){
        this.sp = sp;
    }
    public void ClearAll(){
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isSaved", false);
        editor.putInt("LastSavedQuestion",-1);

        editor.putInt("600", -1);
        editor.putInt("601", -1);
        editor.putInt("602", -1);
        editor.putInt("603", -1);
        editor.putInt("604", -1);
        editor.putInt("605", -1);

        editor.putInt("400", -1);
        editor.putInt("401", -1);
        editor.putInt("402", -1);
        editor.putInt("403", -1);
        editor.putInt("404", -1);

        editor.putInt("501", -1);

        editor.putInt("301", -1);
        // Commit the edits!
        editor.commit();
    }
    public boolean Submit(Context context){
        if(StartTime == null || EndTime == null)
            return false;
        this.context = context;
        new HttpGetTask().execute();
        //clear the local memory
        ClearAll();
        return true;
    }

    public boolean UpdateAnswers(int index,int[] ar){

        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isSaved", true);
        editor.putInt("LastSavedQuestion", index);

        switch (index){
            case 0:
                editor.putInt("600", ar[5]);
                editor.putInt("601", ar[0]);
                editor.putInt("602", ar[1]);
                editor.putInt("603", ar[2]);
                editor.putInt("604", ar[3]);
                editor.putInt("605", ar[4]);
                break;
            case 1:
                editor.putInt("501", ar[0]);
                break;
            case 2:
                editor.putInt("400", ar[0]);
                editor.putInt("401", ar[1]);
                editor.putInt("402", ar[2]);
                editor.putInt("403", ar[3]);
                editor.putInt("404", ar[4]);
                break;
            case 3:
                editor.putInt("301", ar[0]);
                break;
            default:
                break;
        }
        // Commit the edits!
        editor.commit();
        return true;
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
            if(!user.isNetworkAvailable(context.getApplicationContext())){
                ls.add("no connection");
                return ls;
            }
            if(user.isSignedIn){
                //Build up a survey
                //Step 1:  Start with Option, then with Questions, lastly, add question to survey;
                Option op11 = new Option(601, sp.getInt("601", -1),101);
                Option op12 = new Option(602, sp.getInt("602", -1),101);
                Option op13 = new Option(603, sp.getInt("603", -1),101);
                Option op14 = new Option(604, sp.getInt("604", -1),101);
                Option op15 = new Option(605, sp.getInt("605", -1),101);
                Option op16 = new Option(600, sp.getInt("600", -1),101);

                Question q1 = new Question(900);;
                q1.addOption(op11);
                q1.addOption(op12);
                q1.addOption(op13);
                q1.addOption(op14);
                q1.addOption(op15);
                q1.addOption(op16);

                Option op31 = new Option(400, sp.getInt("400", -1), 101);
                Option op32 = new Option(401, sp.getInt("401", -1), 101);
                Option op33 = new Option(402, sp.getInt("402", -1), 101);
                Option op34 = new Option(403, sp.getInt("403", -1), 101);
                Option op35 = new Option(404, sp.getInt("404", -1), 101);

                Option op2 = new Option(501, sp.getInt("501", -1),102);
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

                SurveyHeading head = new SurveyHeading(StartTime, EndTime, 100);
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
                Toast.makeText(context, "Submission fail, please try again!", Toast.LENGTH_SHORT).show();
            }
            else if (result.get(0).toString().equals("no connection"))
            {
                Toast.makeText(context, "No network connection.Please check your network and try later!" , Toast.LENGTH_SHORT).show();
            }
            else {
                //Toast.makeText(WeeklyQ3Direct.this, "Your survey has been submitted!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
