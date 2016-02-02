package app.healthdiary.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import app.healthdiary.R;
import app.healthdiary.SurveyHelper.SurveyAnswer;

public class MainPage extends Activity {

    public void onCreate(Bundle savedInstanceState) {

        // Required call through to Activity.onCreate()
        // Restore any saved instance state
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Set up the application's user interface (content view)
        setContentView(R.layout.mainpage);

        Intent intent = getIntent();
        final SurveyAnswer surveyAnswer = (SurveyAnswer) intent.getSerializableExtra("survey");

        final TextView txt_username=(TextView) findViewById(R.id.txt_name);
        txt_username.setText(surveyAnswer.getStr_UserName());

        final Button btn_StartWeekly = (Button) findViewById(R.id.btn_StartWeekly);
        btn_StartWeekly.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                // Create an explicit Intent for starting the MainPage
                // Activity
                Intent DirectQ1Intent = new Intent(MainPage.this,
                        WeeklyQ1Direct.class);
                DirectQ1Intent.putExtra("surveyanswer",surveyAnswer);
                DirectQ1Intent.putExtra("flag","new");
                // Use the Intent to start the MainPage Activity
                startActivity(DirectQ1Intent);
            }
        });

        final Button btn_Setting = (Button) findViewById(R.id.btn_Setting);
        btn_Setting.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                // Create an explicit Intent for starting the MainPage
                // Activity
                Intent SettingActivity = new Intent(MainPage.this,
                        app.healthdiary.Activities.SettingActivity.class);
                SettingActivity.putExtra("survey",surveyAnswer);
                // Use the Intent to start the MainPage Activity
                startActivity(SettingActivity);
            }
        });
    }
}