package app.healthdiary.SurveyHelper;
//Survey is a class that abstracts a survey
//A survey can contain many different questions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Survey implements Serializable {
    private SurveyHeading myHeading;
    private List<Question> myQuestions;


    public Survey()
    {
        myQuestions = new ArrayList<Question>();
    }

    public void setHeading(final SurveyHeading theHeading)
    {
        myHeading = theHeading;
    }

    public void addQuestion(final Question theQuestion)
    {
        myQuestions.add(theQuestion);
    }


    public List<Question> getQuestions()
    {
        return myQuestions;
    }

    public SurveyHeading getHeading()
    {
        return myHeading;
    }
}
