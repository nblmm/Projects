package app.healthdiary.SurveyHelper;
//User is a class that abstracts a survey taker

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.*;

public class User implements Serializable {

    private String mySessionID = "";
    private List<SurveyHeading> mySurveyHeadings;

    private HttpAgentWithSession myResearch;

    public String getMyUsername() {
        return myUsername;
    }

    private String myUsername;
    private String myPassword;

    public int getN_LocationCollection() {
        return n_LocationCollection;
    }

    public void setN_LocationCollection(int n_LocationCollection) {
        this.n_LocationCollection = n_LocationCollection;
    }

    public int getN_RandomGenerator() {
        return n_RandomGenerator;
    }

    public void setN_RandomGenerator(int n_RandomGenerator) {
        this.n_RandomGenerator = n_RandomGenerator;
    }

    private int n_LocationCollection = 1;
    private int n_RandomGenerator = 1;

    private JSONObject receivedData;

    public boolean isSignedIn;

    public static User getInstance()
    {
        if(instance == null){
            instance = new User();
        }
        return instance;
    }

    private static User instance = null;

    protected User() {

    }

    public void setUserInfo(final String theUsername, final String thePassword)
    {
        myUsername = theUsername;
        myPassword = thePassword;
        myResearch = new HttpAgentWithSession();
        isSignedIn = false;
    }


    public void signin()
    {
        receivedData = myResearch.signin(myUsername, myPassword);
        try {
            if(receivedData.getBoolean("status")) {
                mySessionID = receivedData.getString("sessionid");
                isSignedIn = true;
                System.out.println("Sign in successful");
            } else {
                System.out.println("User not found, maybe use signup() first?");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean submitSurvey(final Survey theSurvey)
    {
        try {
            if (isSignedIn) {
                JSONObject response = myResearch.submitSurvey(theSurvey, mySessionID);
                return response.getBoolean("status");
            } else {
                System.out.println("You must be signed in first");
                return false;
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean uploadFile(final String FileName)
    {
        try {
            if (isSignedIn) {
                JSONObject response = myResearch.uploadFile(FileName, mySessionID);
                return response.getBoolean("status");
            } else {
                System.out.println("You must be signed in first");
                return false;
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Survey getSurvey(final SurveyHeading heading)
    {
        if(isSignedIn){
            Survey mySurvey = new Survey();
            mySurvey.setHeading(heading);
            List<Question> surveyquestions = new ArrayList<Question>();
            try {
                JSONObject response = myResearch.getQuestions(mySessionID, heading.getSurveyID());
                JSONArray questions = response.getJSONArray("questions");
                for (int i = 0; i < questions.length(); i++) {
                    JSONObject json_question = questions.getJSONObject(i);
                    final String qID = json_question.getString("QuestionID");
                    final int qType = json_question.getInt("QuestionType");
                    final String qText = json_question.getString("QuestionText");
                    Question q = new Question(qType, qID, qText);

                    JSONObject options_response = myResearch.getOptions(mySessionID, q.getID());
                    JSONArray options_array = options_response.getJSONArray("options");
                    for (int j = 0; j < options_array.length(); j++) {
                        JSONObject option = options_array.getJSONObject(j);
                        final int opID = option.getInt("OptionID");
                        final int opStatus = option.getInt("OptionStatus");
                        final String opName = option.getString("OptionName");
                        Option newOp = new Option(opID, opStatus, opName);
                        q.addOption(newOp);
                    }
                    mySurvey.addQuestion(q);
                    //			System.out.println("QuestionID: " + qID + "  QuestionType: " + qType + "   QuestionText: " + qText);
                }
                return mySurvey;
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public List<Action> getActions()
    {
        if(isSignedIn) {
            List<Action> actionList = new ArrayList<Action>();
            receivedData = myResearch.getActions(mySessionID);
            try {
                if (receivedData.getBoolean("status")) {
                    JSONArray list = receivedData.getJSONArray("actions");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject json_action = list.getJSONObject(i);
                        Action newAction = new Action(json_action.getString("ActionName"), json_action.getInt("ActionID"));
                        actionList.add(newAction);
                    }
                    return actionList;
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<SurveyHeading> getSurveyHeadings()
    {

        if(isSignedIn) {
            mySurveyHeadings = new ArrayList<SurveyHeading>();
            receivedData = myResearch.getSurveyIDS(mySessionID);
            try {
                if (receivedData.getBoolean("status")) {
                    JSONArray list = receivedData.getJSONArray("id");
                    final List<String> temp = new ArrayList<String>();
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject surveyid = list.getJSONObject(i);
                        SurveyHeading head = new SurveyHeading(surveyid.getString("StartTime"), surveyid.getString("EndTime"), surveyid.getString("SurveyID"));
                        mySurveyHeadings.add(head);
                    }
                    return mySurveyHeadings;
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        } else {
            System.out.println("Please sign in first, use signin() function");
            return null;
        }
    }

    public void signup()
    {
        receivedData = myResearch.signup(myUsername, myPassword);
        try {
            System.out.println(receivedData.getString("message"));
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * determine network connection
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null) {
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }
    //Get the sessionid of the user
    //Note:  if sessionid is empty, the user does not exists
    public String getSessionID()
    {
        return mySessionID;
    }
}
