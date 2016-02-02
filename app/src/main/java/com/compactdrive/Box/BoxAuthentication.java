package com.compactdrive.Box;

import com.compactdrive.Central.Central;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

/**
 * Created by cgadi on 1/29/2016.
 */
public class BoxAuthentication {

    private static String clientId= "3prdvwp4vaaoo4w6dy4e5rux34n8oadt";
    private static String redirect_uri = "http://localhost";
    private static String clientSecret = "Q8L8ALJGcxRpqgGGJJI7gG664iDby2o9";
    public  static String aceToken;
    public static String refToken;
    public static String CODE=null;

    public static String generatePermissionUrl() {
        String response_type = "code";
        String endPoint = "https://accounts.google.com/o/oauth2/auth";
        String finalUrl = endPoint + "?response_type=" + response_type + "&client_id=" +clientId +"&redirect_uri=" +redirect_uri;
        return finalUrl;
    }

    public static void populateTokens(){
        readTokens();
        if(aceToken != null){
            return;
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String endPoint = "https://api.box.com/oauth2/token";
                StringBuffer response = new StringBuffer();
                try {
                    String body_string ="grant_type=authorization_code"+"&code="+CODE+"&client_id="+clientId+"&redirect_uri="+redirect_uri;
                    URL temp = new URL(endPoint);
                    HttpURLConnection con = (HttpURLConnection) temp.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    OutputStream token_stream = con.getOutputStream();
                    token_stream.write(body_string.getBytes());
                    token_stream.flush();
                    token_stream.close();
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    JSONObject o = new JSONObject(response.toString());
                    aceToken = o.getString("access_token");
                    refToken = o.getString("refresh_token");
                    storeTokens();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void readTokens(){
        Properties prop = new Properties();
        InputStream in = null;
        try{
            String storagePath = Central.getLocalDir();
            File dir = new File(storagePath +"/compact drive");
            if(!dir.exists()){
                return;
            }
            String filePath = storagePath +"/compact drive/boxtokens.properties";
            File tokens = new File(filePath);
            boolean fileExists = tokens.exists();
            if(!fileExists){
                return;
            }

            in = new FileInputStream(tokens);
            prop.load(in);
            if(prop != null){

                aceToken = prop.getProperty("BoxacessToken");
                refToken = prop.getProperty("BoxrefreshToken");
            }
        } catch(Exception e){
            e.printStackTrace();
        }finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void storeTokens(){
        Properties prop = new Properties();
        OutputStream out = null;

        try {

            String storagePath = Central.getLocalDir();
            File dir = new File(storagePath + "/compact drive");
            if (!dir.exists()) {
                dir.mkdir();
            }
            String filePath = storagePath + "/compact drive/boxtokens.properties";
            File tokens = new File(filePath);
            if(!tokens.exists()) {
                tokens.createNewFile();
            }

            prop.setProperty("BoxacessToken", aceToken);
            prop.setProperty("BoxrefreshToken",refToken);

            out = new FileOutputStream(filePath);
            prop.store(out,null);
        } catch(Exception e){
            e.printStackTrace();
        }finally {
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
