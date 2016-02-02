package com.compactdrive.OneDrive;

import android.os.Environment;
import android.util.Log;

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
 * Created by cgadi on 12/16/2015.
 */
public class OneDriveAuthentication {
    public static String aceToken;
    public static String refToken;
    public static String code;
    public static final String CLIENTID = "000000004817DF25";
    public static final String CLIENTSECRET="4S09LUCz1Kw4ZZ2erlfv0OxppndBCJHb";
    public static final String REDIRECRURI="http://localhost";


    public static void populateTokens(){

        readTokens();
        if(aceToken != null){
            return;
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String endPoint = "https://login.live.com/oauth20_token.srf";
                StringBuffer response = new StringBuffer();
                try {
                    String body_string ="grant_type=authorization_code"+"&code="+code+"&client_id="+CLIENTID+"&redirect_uri="+REDIRECRURI+"&client_secret="+CLIENTSECRET;
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

    public static void signOut(){
        aceToken="";
        refToken="";
        storeTokens();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String endPoint = "https://login.live.com/oauth20_logout.srf";
                    URL temp = new URL(endPoint);
                    HttpURLConnection con = (HttpURLConnection) temp.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("client_id", CLIENTID);
                    con.setRequestProperty("redirect_uri", REDIRECRURI);
                    int c = con.getResponseCode();
                    Log.i("signOut","Successfully Signed Out");

                }catch (Exception e){
                    Log.e("OneDrive_SignOut",e.getMessage());
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

    public static void refreshToken() {
        readTokens();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String endPoint = "https://login.live.com/oauth20_token.srf";
                StringBuffer response = new StringBuffer();
                try {
                    String body_string ="grant_type=refresh_token"+"&refresh_token="+refToken+"&client_id="+CLIENTID+"&redirect_uri="+REDIRECRURI+"&client_secret="+CLIENTSECRET;
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
                    refToken =o.getString("refresh_token");
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
            String filePath = storagePath +"/compact drive/otokens.properties";
            File tokens = new File(filePath);
            boolean fileExists = tokens.exists();
            if(!fileExists){
                return;
            }

            in = new FileInputStream(tokens);
            prop.load(in);
            if(prop != null){

                aceToken = prop.getProperty("oneDriveAcessToken");
                refToken = prop.getProperty("oneDriveRefreshToken");
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
            String filePath = storagePath + "/compact drive/otokens.properties";
            File tokens = new File(filePath);
            tokens.createNewFile();

            prop.setProperty("oneDriveAcessToken", aceToken);
            prop.setProperty("oneDriveRefreshToken",refToken);

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
