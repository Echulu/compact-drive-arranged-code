package com.compactdrive.GoogleDrive;

import android.util.Log;

import com.compactdrive.AppUtils.CDFileObject;
import com.compactdrive.AppUtils.UnAuthorizedException;
import com.compactdrive.OneDrive.OneDriveAuthentication;
import com.compactdrive.OneDrive.OneDriveUtils;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by cgadi on 12/24/2015.
 */
public class GoogleGetChildrenThread extends Thread{

    @Override
    public void run() {
        try {

            String endpoint = "https://www.googleapis.com/drive/v2/files";
            HttpURLConnection con = GoogleDriveUtils.prepareConnection(endpoint);

            if (con.getResponseCode() == 200) {
                GoogleDriveUtils.getChildrenResponseAsString(con);
                GoogleDriveUtils.populateTree();
            }else if(con.getResponseCode() == 401){
                GoogleDriveAuthentication.refreshToken();
                con = GoogleDriveUtils.prepareConnection(endpoint);
                if(con.getResponseCode() != 200 ) throw new UnAuthorizedException("The Http Response Code is: "+con.getResponseCode());
                GoogleDriveUtils.getChildrenResponseAsString(con);
                GoogleDriveUtils.populateTree();
            }
        }catch (Exception e){
            Log.e("getChildrenThread", e.getMessage());
        }

    }

}
