package com.compactdrive.OneDrive;

import android.util.Log;

import com.compactdrive.AppUtils.CDFileObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by cgadi on 12/17/2015.
 */
public class OneDriveUtils {
    public static StringBuffer fileJson;

    public static HttpURLConnection prepareConnection(String endPoint) {
        try {
            URL temp = new URL(endPoint);
            HttpURLConnection con = (HttpURLConnection) temp.openConnection();
            con.setRequestMethod("GET");
            String authToken = "Bearer " + OneDriveAuthentication.aceToken;
            con.setRequestProperty("Authorization", authToken);
            int c = con.getResponseCode();
            return con;
        } catch (Exception e) {
            Log.e("OneDrive", e.getMessage());
        }

        return null;
    }

    public static void getChildrenResponseAsString(HttpURLConnection con){
        try {
            fileJson = new StringBuffer();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                fileJson.append(inputLine);
            }
        }catch (Exception e){
            Log.e("OneDrive",e.getMessage());
        }
    }

    public static ArrayList<CDFileObject> extractChildren(String fileJson){
        try {
            JSONObject raw = new JSONObject(fileJson);
            JSONArray children = raw.getJSONArray("value");
            ArrayList<CDFileObject> result = new ArrayList<CDFileObject>();
            for (int i=0;i<children.length();i++ ) {
                JSONObject eachChild = children.getJSONObject(i);
                CDFileObject child = new CDFileObject();
                child.setID("2" + eachChild.getString("id"));
                child.setParentId(eachChild.getJSONObject("parentReference").getString("id"));
                child.setDoc(eachChild.getString("createdDateTime"));
                child.setDom(eachChild.getString("lastModifiedDateTime"));
                child.setTitle(eachChild.getString("name"));
                try{
                    eachChild.getJSONObject("file");
                    child.isFolder=false;
                    child.setMimeType(eachChild.getJSONObject("file").getString("mimeType"));
                    child.setUrl(eachChild.getString("@content.downloadUrl"));
                    child.setSize(eachChild.getInt("size"));
                }catch(JSONException e){
                    child.isFolder = true;
                }
                result.add(child);
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<CDFileObject> getChildren(final String Id){
        OneDriveGetChildrenThread t = new OneDriveGetChildrenThread(Id);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            Log.e("getChildren",e.getMessage());
        }
        //need to decide if it truely required
        while(true){
            if(t.getState()==Thread.State.TERMINATED){
                break;
            }
        }
        return t.getChilds();
    }

}
