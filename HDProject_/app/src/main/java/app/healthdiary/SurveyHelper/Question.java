package app.healthdiary.SurveyHelper;
//Question is a class that abstracts a question in a survey

import java.util.ArrayList;
import java.util.List;

public class Question {

    private int myType;
    private String myID;
    private String myName;
    private List<Option> myOptions;


    public Question(int theType, final String theID, final String theName)
    {
        myType = theType;
        myID = theID;
        myName = theName;
        myOptions = new ArrayList<Option>();
    }

    public Question(int theType)
    {
        myType = theType;
        myID = "";
        myOptions = new ArrayList<Option>();
        myName = "";
    }

    public String getName()
    {
        return myName;
    }

    public void addOption(final Option theOption)
    {
        myOptions.add(theOption);
    }

    public String getID()
    {
        return myID;
    }

    public int getType()
    {
        return myType;
    }


    public List<Option> getOptions()
    {
        return myOptions;
    }

}
