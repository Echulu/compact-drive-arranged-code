package com.compactdrive.AppUtils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URL;

/**
 * Created by cgadi on 12/24/2015.
 */
public class CDFileObject {
    private String id=null;
    private String title=null;
    private JSONArray owner = new JSONArray();
    private String dateOfCreation = null;
    private String dateOfModification = null;
    private String mimeType =null;
    private long size ;
    private String parentId = null;
    private URL url=null;
    public boolean isFolder;
    public String getID(){
        return id;
    }
    public void setID(String id){
        this.id=id;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getOwners(){
        StringBuffer tmp = new StringBuffer();
        for(int i=0;i<owner.length();i++)
            try {
                tmp=tmp.append(owner.get(i).toString()+",");
            } catch (JSONException e) {
                Log.i("Ex Gfo", e.getMessage());
            }
        String temp = tmp.substring(0,tmp.length()-1);
        return temp;
    }
    public void setOwners(JSONArray owner){
        this.owner = owner;
    }
    public String getDom(){
        return dateOfModification;
    }
    public void setDom(String temp){
        this.dateOfModification=temp;
    }
    public String getDoc(){
        return dateOfCreation;
    }
    public void setDoc(String doc){
        this.dateOfCreation=doc;
    }
    public String getMimeType(){
        return mimeType;
    }
    public void setMimeType(String mime){
        this.mimeType=mime;
    }
    public String getParentId() {
        return parentId;
    }
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    public void setUrl(String url) {
        try {
            this.url = new URL(url);
        } catch (Exception e) {
            Log.i("setURL",e.getMessage());
        }
    }
    public URL getUrl() {
        return url;
    }
    public long getSize() {
        return size;
    }
    public void setSize(long size) {
        this.size = size;
    }
}
