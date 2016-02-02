package app.healthdiary.SurveyHelper;

import java.io.Serializable;

/**
 * Created by hongyliu on 7/21/15.
 */
public class SurveyAnswer implements Serializable {
    public String getStr_UserName() {
        return str_UserName;
    }

    public void setStr_UserName(String str_UserName) {
        this.str_UserName = str_UserName;
    }

    private String str_UserName = "";

    public String getStr_Password() {
        return str_Password;
    }

    public void setStr_Password(String str_Password) {
        this.str_Password = str_Password;
    }

    public String getStr_SessionID() {
        return str_SessionID;
    }

    public void setStr_SessionID(String str_SessionID) {
        this.str_SessionID = str_SessionID;
    }

    public String getStr_StartTime() {
        return str_StartTime;
    }

    public void setStr_StartTime(String str_StartTime) {
        this.str_StartTime = str_StartTime;
    }

    public String getStr_EndTime() {
        return str_EndTime;
    }

    public void setStr_EndTime(String str_EndTime) {
        this.str_EndTime = str_EndTime;
    }

    public String getStr_SymptomDescription() {
        return str_SymptomDescription;
    }

    public void setStr_SymptomDescription(String str_SymptomDescription) {
        this.str_SymptomDescription = str_SymptomDescription;
    }

    public int[] getbSympton() {
        return bSympton;
    }

    public void setbSympton(int[] bSympton) {
        this.bSympton = bSympton;
    }

    public int getN_SymptomsinFamily() {
        return n_SymptomsinFamily;
    }

    public void setN_SymptomsinFamily(int n_SymptomsinFamily) {
        this.n_SymptomsinFamily = n_SymptomsinFamily;
    }

    public int getN_SymptomsNonFamilyCategory() {
        return n_SymptomsNonFamilyCategory;
    }

    public void setN_SymptomsNonFamilyCategory(int n_SymptomsNonFamilyCategory) {
        this.n_SymptomsNonFamilyCategory = n_SymptomsNonFamilyCategory;
    }

    public String getStr_Actions() {
        return str_Actions;
    }

    public void setStr_Actions(String str_Actions) {
        this.str_Actions = str_Actions;
    }

    public int[] getbActions() {
        return bActions;
    }

    public void setbActions(int[] bActions) {
        this.bActions = bActions;
    }

    private String str_Password = "";
    private String str_SessionID = "";
    private String str_StartTime = "";
    private String str_EndTime = "";
    private String str_SymptomDescription = "";
    private int[] bSympton = null;
    private int n_SymptomsinFamily=0;
    private int n_SymptomsNonFamilyCategory=-1;
    private String str_Actions = "";
    private int[] bActions = null;

    public SurveyAnswer(){};

    public SurveyAnswer(String username, String password, String sessionid, String start, String end){
        str_UserName = username;
        str_Password = password;
        str_SessionID = sessionid;
        str_StartTime = start;
        str_EndTime = end;
    }
}
