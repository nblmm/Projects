package app.healthdiary.Helper;

import android.content.SharedPreferences;
import android.os.Environment;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.lang.*;
import java.util.Date;

import app.healthdiary.SurveyHelper.User;

/**
 * Created by Hongyuan on 2/24/2016.
 */
public class FileUploader extends Thread {
    public static final int BUFFER_SIZE = 100;

    public void setUsername(String username) {
        this.username = username;
    }

    public String username = "";
    //Socket Solution
    public void run(){
        String file_name;
        String urlServer = "52.27.225.80";
        int port = 3332;
        String timeStamp = new Date().getTime() + "";
        File BaseDir;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            BaseDir = Environment.getExternalStorageDirectory();//get SDCard path
        }
        else
        {
            BaseDir = Environment.getDataDirectory();//get local path
        }
        //first, backup the file to be uploaded and empty the original file that keeps accepting new data
        file_name = BaseDir.getPath() + "/HealthDiary_data/Location_" + username + ".txt";
        File file = new File(file_name);
        if(file.length() > 20 * 1024) {
            if (file.renameTo(new File(BaseDir.getPath() + "/HealthDiary_data/Location_" + username + "_" + timeStamp + "_bak.txt"))) {
                if (!file.delete()) {
                    System.out.println("fail to delete the file");
                }
            }
        }
        //upload all files with "bak" mark. These files might be ones that have not been uploaded successfully previously
        File folder = new File(BaseDir.getPath() + "/HealthDiary_data");
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++){
            if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains("bak")) {
                file = new File(listOfFiles[i].getAbsolutePath());

                long length = file.length();
                if (length <= 0)
                    return;
                System.out.println(length);
                Socket socket;

                try {
                    System.out.println("Connected server at " + urlServer + ":" + port);
                    socket = new Socket(urlServer, port);
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                    oos.writeObject(file.getName());

                    FileInputStream fis = new FileInputStream(file);
                    byte [] buffer = new byte[BUFFER_SIZE];
                    Integer bytesRead;

                    while ((bytesRead = fis.read(buffer)) > 0) {
                        oos.writeObject(bytesRead);
                        oos.writeObject(Arrays.copyOf(buffer, buffer.length));
                    }
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    String response = in.readUTF();
                    long recvLenght = Long.parseLong(response.split(" ")[5]);

                    File bakDir = new File(BaseDir
                            .getPath() + "/HealthDiary_data/bak");
                    if (!bakDir.exists()) {
                        if(!bakDir.mkdir()){
                            System.out.println("fail to make the folder");
                        }
                    }
                    if(length <= recvLenght){
                        //zip the datafile
                        if(file.renameTo(new File(BaseDir.getPath() + "/HealthDiary_data/bak/Location_" + username + "_uploaded_"+ timeStamp +".txt")))
                            if(!file.delete()){
                                System.out.println("fail to delete the file");
                            }
                    }
                    oos.close();
                    ois.close();
                    //System.exit(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //HTTP solution
    public void uploadFile(){
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        DataInputStream inputStream = null;

        String pathToOurFile = "/HealthDiary_data/Location.txt";
        String urlServer = "http://192.168.1.1/handle_upload.php";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;

        try
        {
            File BaseDir;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                BaseDir = Environment.getExternalStorageDirectory();//get SDCard path
            }
            else
            {
                BaseDir = Environment.getDataDirectory();//get local path
            }
            pathToOurFile = BaseDir.getPath() + "/HealthDiary_data/Location.txt";


            FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );

            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Enable POST method
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/formdata;boundary="+boundary);

            outputStream = new DataOutputStream( connection.getOutputStream() );
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data;name=\"uploadedfile\";filename=\"" + pathToOurFile +"\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0)
            {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            int serverResponseCode = connection.getResponseCode();
            System.out.println("serverResponseCode: " + serverResponseCode);
            String serverResponseMessage = connection.getResponseMessage();
            System.out.println("serverResponseMessage: " + serverResponseMessage);
            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
        }
        catch (Exception ex)
        {
            //Exception handling
        }
    }
}
