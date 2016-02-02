package app.healthdiary.SurveyHelper;

/**
 * Created by Hongyuan on 7/19/2015.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.*;
public class HttpAgentWithSession {

    private String localTestUrl = "http://localhost:8080/";
    private String stagingURL = "http://***.***.***.***:8080/";

    private String url = stagingURL;

    public HttpAgentWithSession()
    {

    }

    //Create a new user, good for new user signup
    public JSONObject signup(final String username, final String password){
        final String api = "api/signup";

        //Post object parameter
        JSONObject userSignupInfo = new JSONObject();
        try {
            userSignupInfo.put("username", username);
            userSignupInfo.put("password", password);

            //Response data as JSONobject
            return new JSONObject(callAPI(userSignupInfo, api));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Login user using their name and password
    //Parameter:  theUsername(String), thePassword(String)
    //Return:  JSONObject for response object
    public JSONObject signin(final String theUsername, final String thePassword) {

        final String api  = "api/signin";

        //Post object parameter
        JSONObject userSigninInfo = new JSONObject();
        try {
            userSigninInfo.put("username", theUsername);
            userSigninInfo.put("password", thePassword);
            String para = callAPI(userSigninInfo, api);
            return new JSONObject(para);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getActions(final String theSessionID)
    {
        final String api = "api/getactions";

        JSONObject myinfo = new JSONObject();
        try {
            myinfo.put("sessionid", theSessionID);

            return new JSONObject(callAPI(myinfo, api));
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    //Get all the survey id from a user
    //Parameter:  theSessionID(String)
    //Return:  JSONObject for response object
    public JSONObject getSurveyIDS(final String theSessionID)
    {
        final String api = "api/getsurveyids";

        JSONObject myinfo = new JSONObject();
        try {
            myinfo.put("sessionid", theSessionID);
            return new JSONObject(callAPI(myinfo, api));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
/*
    //Get the survey result from a survey id
    //Parameter:  theSessionID(String), theSurveyID(String)
    //Return:  JSONObject for response object
    public JSONObject getSurveyResult(final String theSessionID, final String theSurveyID)
    {
        final String api = "api/getsurveyresult";

        JSONObject data = new JSONObject();
        try {
            data.put("surveyid", theSurveyID);
            data.put("sessionid", theSessionID);
            return new JSONObject(callAPI(data, api));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
*/

    public JSONObject getQuestions(final String theSessionID, final String theSurveyID)
    {
        final String api = "api/getquestions";

        JSONObject data = new JSONObject();
        try {
            data.put("sessionid", theSessionID);
            data.put("surveyid", theSurveyID);

            return new JSONObject(callAPI(data, api));
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public JSONObject getOptions(final String theSessionID, final String theQuestionID)
    {
        final String api = "api/getoptions";

        JSONObject data = new JSONObject();
        try {
            data.put("sessionid", theSessionID);
            data.put("questionid", theQuestionID);

            return new JSONObject(callAPI(data, api));
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Submit a survey to the server
    //Parameter:  theSessionID(String), theStartTime(String), theEndTime(String), theQuestionList(String)
    //Return:  JSONObject for response object
    public JSONObject submitSurvey(final Survey theSurvey, final String theSessionID)
    {
        final String api = "api/submitsurvey";
        boolean levelOneCompleted = false;
        JSONObject finalresponse = new JSONObject();

        int qcount = 0; //quesiton count
        int opcount = 0; //option count;
        String surveyid = "";
        //Level 0
        //WeeklySymptomSurvey
        JSONObject level0 = new JSONObject();
        try {
            level0.put("entrylevel", 0);
            level0.put("starttime", theSurvey.getHeading().getStartDate());
            level0.put("endtime", theSurvey.getHeading().getEndDate());
            level0.put("sessionid", theSessionID);
            level0.put("surveytype", theSurvey.getHeading().getN_SurveyType());

            JSONObject stage1 = new JSONObject(callAPI(level0, api));
            if(stage1.getBoolean("status"))
            {
                levelOneCompleted = true;
                surveyid = stage1.getString("surveyid");
            }

            //Level 1 & 2
            //SurveyQuestionBridge
            if(levelOneCompleted)
            {
                List<Question> questions = theSurvey.getQuestions();
                for(int i = 0; i < questions.size(); i++)
                {
                    Question myQuestion = questions.get(i);
                    JSONObject myType = new JSONObject();
                    myType.put("entrylevel", 1);
                    myType.put("sessionid", theSessionID);
                    myType.put("surveyid", surveyid);
                    myType.put("questiontype", myQuestion.getType());

                    JSONObject response = new JSONObject(callAPI(myType, api));
                    if(response.getBoolean("status")) {
                        qcount++;
                        final String questionid = response.getString("questionid");

                        //level 2
                        JSONObject finalLevel = new JSONObject();
                        finalLevel.put("sessionid", theSessionID);
                        finalLevel.put("questionid", questionid);
                        finalLevel.put("entrylevel", 2);

                        JSONArray jsonoption = new JSONArray();
                        List<Option> options = myQuestion.getOptions();
                        for(int j = 0; j < options.size(); j++)
                        {
                            Option op = options.get(j);
                            JSONObject json_op = new JSONObject();
                            json_op.put("id", op.getID());
                            json_op.put("status", op.getStatus());
                            json_op.put("type", op.getType());
                            jsonoption.put(j, json_op);
                        }
                        finalLevel.put("options", jsonoption);

                        JSONObject optionResponse = new JSONObject(callAPI(finalLevel, api));
                        if(optionResponse.getBoolean("status")) {
                            opcount++;
                        }
                    }

                }
                int totaloptions = 0;
                for(Question q : questions)
                {
                    totaloptions += q.getOptions().size();
                }
                if(qcount == questions.size() && opcount == totaloptions)
                {
                    finalresponse.put("status",  true);
                    finalresponse.put("message", "Successfully submitted survey");
                } else {
                    finalresponse.put("status",  false);
                    finalresponse.put("message", "Failed to submit survey");
                }
                return finalresponse;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    //Get the symptom name with their corresponding id number
    //symptom id is used for constructing survey using json object
    public void getAllSymptoms()
    {
        final String api = "api/getallsymptoms";

        JSONArray result = null;
        try {
            result = new JSONArray(callAPI(new JSONObject(), api));
            for(int i = 0; i < result.length(); i++) {
                JSONObject row = result.getJSONObject(i);
                System.out.println(row.get("SymptomID") + "   " +  row.get("SymptomName"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //A general method for calling api and receiving response
    private String callAPI(final JSONObject param, final String api)
    {
        URL apiURL;
        try {

            apiURL = new URL(url + api);
            HttpURLConnection conn;

            conn = (HttpURLConnection) apiURL.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(param.toString());
            wr.flush();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
//			System.out.println(response.toString());
            return response.toString();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
