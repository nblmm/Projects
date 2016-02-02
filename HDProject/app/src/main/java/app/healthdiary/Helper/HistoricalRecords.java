package app.healthdiary.Helper;

/**
 * Created by Hongyuan on 1/1/2016.
 */
public class HistoricalRecords {
    private int m_Num = 0;
    private int m_Ind = 0;
    //record the sum of the incoming vector and the last saved vector, as an indicator of the speed
    private double [][] m_Records;
    private double [][] m_Angles;
    public HistoricalRecords(int num){
        m_Num = num;
        m_Records = new double[num][3];
        m_Angles = new double [num][num];
        for(int i = 0; i < num; i++) {
            for (int j = 0; j < 3; j++)
                m_Records[i][j] = 0;
            for (int j = 0; j < num; j++)
                m_Angles[i][j] = 0;
        }
    }
    public void Reset(){
        for(int i = 0; i < m_Num; i++) {
            for (int j = 0; j < 3; j++)
                m_Records[i][j] = 0;
            for (int j = 0; j < m_Num; j++)
                m_Angles[i][j] = 0;
        }
    }
    public void IndexIncrement(){
        m_Ind = (++ m_Ind) % m_Num;
    }
    //replace the oldest record with a new coming data record
    public void setNewRecord(double [] value){
        int preindex = (m_Ind + m_Num - 1) % m_Num;
        //System.out.println("preindex : "+ preindex);
        if(Math.abs(value[0]) > 1 || Math.abs(value[1]) > 1 || Math.abs(value[2]) > 1){
            for (int i = 0; i < 3; i++) {
                m_Records[m_Ind][i] = value[i] + m_Records[preindex][i];
                //System.out.println("value ["+i+"]: "+ value[i]);
                //System.out.println("Pre m_Records ["+preindex+"]["+i+"]: "+ m_Records[preindex][i]);
                //System.out.println("m_Records ["+m_Ind+"]["+i+"]: "+ m_Records[m_Ind][i]);
            }
        }
        else{
            for (int i = 0; i < 3; i++)
                m_Records [m_Ind][i] = m_Records[preindex][i];
        }
        //update the angles
        getAllAngles();
        //increase the index
        IndexIncrement();
    }
    public void getAllAngles(){
        for (int i = 1; i < m_Num; i++){
            int ind = (m_Ind + i) % m_Num;
            double angle = getAngle (ind);
            //System.out.println("Angle ["+m_Ind+"]["+ind+"]: "+angle);
            if(angle > 180 || angle < 0)
                return;
            if (ind > m_Ind)
                m_Angles[m_Ind][ind] = angle;
            else
                m_Angles[ind][m_Ind] = angle;
        }
    }
    //return a angle between two vectors, the value of the angle: [0, 180]
    public double getAngle(int Index1){
        //if the index is out of range, return a nonsense value
        if(Index1 > m_Num)
            return -1000.0;
        if((m_Records[Index1][0]==0&&m_Records[Index1][1]==0&&m_Records[Index1][2]==0)||(m_Records[m_Ind][0]==0&&m_Records[m_Ind][1]==0&&m_Records[m_Ind][2]==0))
            return 0;
        double cos =(m_Records[Index1][0] * m_Records[m_Ind][0]
                    + m_Records[Index1][1] * m_Records[m_Ind][1]
                    + m_Records[Index1][2] * m_Records[m_Ind][2])
                    / (Math.sqrt(m_Records[Index1][0]*m_Records[Index1][0] + m_Records[Index1][1]*m_Records[Index1][1] + m_Records[Index1][2]*m_Records[Index1][2])
                    * Math.sqrt(m_Records[m_Ind][0]*m_Records[m_Ind][0] + m_Records[m_Ind][1]*m_Records[m_Ind][1] + m_Records[m_Ind][2]*m_Records[m_Ind][2]));
        //System.out.println("m_Records ["+m_Ind+"][0]: "+ m_Records[m_Ind][0] + ", m_Records ["+Index1+"][0]: "+ m_Records[Index1][0]);
        //System.out.println("m_Records ["+m_Ind+"][1]: "+ m_Records[m_Ind][1] + ", m_Records ["+Index1+"][1]: "+ m_Records[Index1][1]);
        //System.out.println("m_Records ["+m_Ind+"][2]: "+ m_Records[m_Ind][2] + ", m_Records ["+Index1+"][2]: "+ m_Records[Index1][2]);
        //System.out.println("Angle: "+cos);
        if(cos > 1)
            cos = 1;
        if(cos < -1)
            cos = -1;
        return Math.toDegrees(Math.acos(cos));
    }
    public Boolean bLargeAngle(){
        String s = "";
        for (int i = 0; i < m_Num; i++) {
            for (int j = 0; j < m_Num; j++)
                s = s + Math.round(m_Angles[i][j]) + ", ";
            System.out.println(s);
            s = "";
        }
        System.out.println("               ");
        for (int i = 0; i < m_Num; i++)
            for (int j = i + 1; j < m_Num; j ++)
                if (m_Angles[i][j] > 60)
                    return true;
        return false;
    }
    /*
    public double Mean(){
        double sum = 0;
        for(int i = 0; i < m_Records.length; i++){
            sum += m_Records [i];
        }
        return sum/m_Records.length;
    }
    */
}
