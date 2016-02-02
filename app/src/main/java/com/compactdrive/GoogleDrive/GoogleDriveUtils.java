package com.compactdrive.GoogleDrive;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cgadi on 12/24/2015.
 */
public class GoogleDriveUtils {
    public static StringBuffer fileJson;
    private static Map<String,List<JSONArray>> childrenByParent = new HashMap<String,List<JSONArray>>();

    public static void populateTree(){
        try {
            JSONObject rawResp = new JSONObject(fileJson.toString());
            childrenByParent.clear();
            JSONArray allFiles = rawResp.getJSONArray("items");
            for(int i=0;i<allFiles.length();i++){
                JSONObject eachFile = (JSONObject)allFiles.get(i);
                Boolean isDir = eachFile.getString("mimeType").equals("application/vnd.google-apps.folder")?true:false;
                JSONArray parents = eachFile.getJSONArray("parents");
                for (int j=0;j<parents.length();j++){
                    JSONObject eachParent = (JSONObject)parents.get(j);
                    String eachParentId;
                    if (eachParent.getBoolean("isRoot")){
                        eachParentId = "root";
                    }else{
                        eachParentId = eachParent.getString("id");
                    }
                    List<JSONArray> children = childrenByParent.get(eachParentId);
                    if(children == null){
                        children = new ArrayList<JSONArray>();
                        childrenByParent.put(eachParentId,children);
                        children = childrenByParent.get(eachParentId);
                    }
                    if(isDir){
                        JSONArray dirChildren;
                        try {
                            dirChildren = children.get(0);
                        }catch(IndexOutOfBoundsException iobe){
                            dirChildren = new JSONArray();
                            children.add(dirChildren);
                            dirChildren = children.get(0);
                        }
                        dirChildren.put(eachFile);
                    }else{
                        JSONArray fileChildren;
                        try {
                            fileChildren = children.get(1);
                        }catch(IndexOutOfBoundsException iobe){
                            try {
                                fileChildren = children.get(0);
                            }catch(IndexOutOfBoundsException iob) {
                                fileChildren = new JSONArray();
                                children.add(fileChildren);
                            }
                            fileChildren = new JSONArray();
                            children.add(fileChildren);
                            fileChildren = children.get(1);
                        }
                        fileChildren.put(eachFile);
                    }
                }
            }
        } catch (JSONException je){
            Log.i("tree framing exception", je.getMessage());
        }
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
            Log.e("GoogleDrive", e.getMessage());
        }
    }

    private static JSONArray getChildrenByParentAsJsonArray(String parentId) {
        List<JSONArray> jsonChildren =childrenByParent.get(parentId);
        List<CDFileObject> children = new ArrayList<CDFileObject>();
        JSONArray finalChildren = new JSONArray();
        if(jsonChildren != null) {
            try {
                JSONArray dirChildren = jsonChildren.get(0);
                JSONArray fileChildren = jsonChildren.get(1);
                for (int i=0;i<dirChildren.length();i++){
                    finalChildren.put(dirChildren.getJSONObject(i));
                }
                for (int i=0;i<fileChildren.length();i++){
                    finalChildren.put(fileChildren.getJSONObject(i));
                }

            } catch (Exception e) {
                Log.e("GoogleDriveUtils", e.getMessage());
            }
        }
        return finalChildren;

    }

    public static ArrayList<CDFileObject> getChildrenByParent(String parent)
    {
        JSONArray fileList = getChildrenByParentAsJsonArray(parent);
        ArrayList<CDFileObject> resultList = new ArrayList<>();
        try {
            int fileCount = 0;
            while (fileCount < fileList.length()) {
                JSONObject temp = (JSONObject) fileList.get(fileCount);

                JSONObject label = (JSONObject) temp.get("labels");
                JSONArray parents = (JSONArray) temp.get("parents");
                    if (label.getString("trashed").equals("false")) {
                        CDFileObject tem = new CDFileObject();
                        tem.setID("1" + temp.getString("id"));
                        tem.setTitle(temp.getString("title"));
                        tem.setMimeType(temp.getString("mimeType"));
                        tem.setOwners(temp.getJSONArray("ownerNames"));
                        tem.setDom(temp.getString("modifiedDate"));
                        if(!tem.getMimeType().equals("application/vnd.google-apps.folder")) {
                            tem.setUrl(temp.getString("downloadUrl"));
                            tem.setSize(temp.getLong("fileSize"));
                            tem.isFolder = false;
                        }else {
                            tem.isFolder = true;
                        }
                        tem.setDoc(temp.getString("createdDate"));
                        tem.setParentId(parent);
                        resultList.add(tem);
                    }
                fileCount++;
                }
                fileCount++;
        }
        catch (Exception e){
            Log.i("Error",e.getMessage());
        }
        return resultList;
    }

    public static HttpURLConnection prepareConnection(String endPoint){
        try {
            URL temp = new URL(endPoint);
            HttpURLConnection con = (HttpURLConnection) temp.openConnection();
            con.setRequestMethod("GET");
            String authToken = "OAuth " + GoogleDriveAuthentication.aceToken;
            con.setRequestProperty("Authorization", authToken);
            int c = con.getResponseCode();
            System.out.print(c);
            return con;

        } catch (Exception e) {
            Log.e("GoogleDriveUtils", e.getMessage());
            return null;
        }
    }
}
