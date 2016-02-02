//package com.compactdrive.Box;
//
//import android.util.Log;
//
//import com.compactdrive.AppUtils.CDFileObject;
//import com.compactdrive.GoogleDrive.GoogleDriveAuthentication;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by cgadi on 1/29/2016.
// */
//public class BoxUtils {
//    public static StringBuffer fileJson;
//    private static String currentParent;
//
//    public static List<CDFileObject> getChildren(String Id){
//        currentParent = Id;
//
//    }
//
//    public static void getChildrenResponseAsString(HttpURLConnection con){
//        try {
//            fileJson = new StringBuffer();
//            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            String inputLine;
//            while ((inputLine = in.readLine()) != null) {
//                fileJson.append(inputLine);
//            }
//        }catch (Exception e){
//            Log.e("OneDrive",e.getMessage());
//        }
//    }
//
//    public static ArrayList<CDFileObject> extractChildren(String fileJson){
//        try {
//            JSONObject raw = new JSONObject(fileJson);
//            JSONArray children = raw.getJSONArray("value");
//            ArrayList<CDFileObject> result = new ArrayList<CDFileObject>();
//            for (int i=0;i<children.length();i++ ) {
//                JSONObject eachChild = children.getJSONObject(i);
//                CDFileObject child = new CDFileObject();
//                child.setID("3" + eachChild.getString("id"));
//                child.setParentId(currentParent);
//                child.setTitle(eachChild.getString("name"));
//                try{
//                    eachChild.getJSONObject("file");
//                    child.isFolder=false;
//                    child.setMimeType(eachChild.getJSONObject("file").getString("mimeType"));
//                    child.setUrl(eachChild.getString("@content.downloadUrl"));
//                    child.setSize(eachChild.getInt("size"));
//                }catch(JSONException e){
//                    child.isFolder = true;
//                }
//                result.add(child);
//            }
//            return result;
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public static HttpURLConnection prepareConnection(String endPoint){
//        try {
//            URL temp = new URL(endPoint);
//            HttpURLConnection con = (HttpURLConnection) temp.openConnection();
//            con.setRequestMethod("GET");
//            String authToken = "Bearer " + BoxAuthentication.aceToken;
//            con.setRequestProperty("Authorization", authToken);
//            int c = con.getResponseCode();
//            return con;
//
//        } catch (Exception e) {
//            Log.e("BoxUtils", e.getMessage());
//            return null;
//        }
//    }
//}
