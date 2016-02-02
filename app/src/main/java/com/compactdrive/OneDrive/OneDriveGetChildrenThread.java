package com.compactdrive.OneDrive;

import android.util.Log;

import com.compactdrive.AppUtils.CDFileObject;
import com.compactdrive.AppUtils.UnAuthorizedException;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by cgadi on 12/18/2015.
 */
public class OneDriveGetChildrenThread extends Thread {

    private String Id;
    public ArrayList<CDFileObject> children;
    public OneDriveGetChildrenThread(String Id) {
        this.Id = Id;
    }
    @Override
    public void run() {
        try {
            String endpoint = "https://api.onedrive.com/v1.0/drive/items/" + Id + "/children";
            HttpURLConnection con = OneDriveUtils.prepareConnection(endpoint);
            if (con.getResponseCode() == 200) {
                OneDriveUtils.getChildrenResponseAsString(con);
                children = OneDriveUtils.extractChildren(OneDriveUtils.fileJson.toString());
            }else if(con.getResponseCode() == 401){
                OneDriveAuthentication.refreshToken();
                con = OneDriveUtils.prepareConnection(endpoint);
                if(con.getResponseCode() != 200 ) throw new UnAuthorizedException("Error Code: 401");
                OneDriveUtils.getChildrenResponseAsString(con);
                children = OneDriveUtils.extractChildren(OneDriveUtils.fileJson.toString());
            }
        }catch (Exception e){
            Log.e("getChildrenThread",e.getMessage());
        }
    }

    public ArrayList<CDFileObject> getChilds() {
        return children;
    }
}
