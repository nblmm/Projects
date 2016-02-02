package app.healthdiary.SurveyHelper;

/**
 * Created by Hongyuan on 7/28/2015.
 */
public class Option {
    final public int OPTIONTYPE1 = 101;
    final public int OPTIONTYPE2 = 102;

    private int myID;
    private int myType;
    private int myStatus;
    private String myName;

    public Option(int theID, int theStatus, final String theName, int theType)
    {
        myID = theID;
        myStatus = theStatus;
        myName = theName;
        myType = theType;
    }

    public Option(int theID, int theStatus, final String theName)
    {
        myID = theID;
        myStatus = theStatus;
        myName = theName;
    }

    public Option(int theID, int theStatus, int theType)
    {
        myID = theID;
        myStatus = theStatus;
        myType = theType;
        myName = "";
    }

    public Option(int theID, int theStatus)
    {
        myID = theID;
        myStatus = theStatus;
        myName = "";
    }

    public String getName()
    {
        return myName;
    }

    public int getType()
    {
        return myType;
    }


    public int getID()
    {
        return myID;
    }

    public int getStatus()
    {
        return myStatus;
    }
}
