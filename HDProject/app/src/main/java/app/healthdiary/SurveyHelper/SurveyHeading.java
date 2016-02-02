package app.healthdiary.SurveyHelper;

/**
 * Created by Hongyuan on 7/28/2015.
 */
public class SurveyHeading {
    private String myStartDate = "";
    private String myEndDate = "";
    private String mySurveyID = "";
    private int n_SurveyType;
    public int getN_SurveyType() {
        return n_SurveyType;
    }

    public void setN_SurveyType(int n_SurveyType) {
        this.n_SurveyType = n_SurveyType;
    }

    public SurveyHeading(final String theStartDate, final String theEndDate, final String theSurveyID)
    {
        myStartDate = theStartDate;
        myEndDate = theEndDate;
        mySurveyID = theSurveyID;
    }

    public SurveyHeading(final String theStartDate, final String theEndDate, int SurveyType)
    {
        n_SurveyType = SurveyType;
        myStartDate = theStartDate;
        myEndDate = theEndDate;
    }

    public String getSurveyID()
    {
        return mySurveyID;
    }

    public String getStartDate()
    {
        return myStartDate;
    }

    public String getEndDate()
    {
        return myEndDate;
    }
}
