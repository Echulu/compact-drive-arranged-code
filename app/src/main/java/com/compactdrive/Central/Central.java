package com.compactdrive.Central;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.compactdrive.AppUtils.About;
import com.compactdrive.AppUtils.CDFileObject;
import com.compactdrive.AppUtils.FileAdapter;
import com.compactdrive.AppUtils.fileDownload;
import com.compactdrive.GoogleDrive.GetCode_GoogleDrive;
import com.compactdrive.GoogleDrive.GoogleDriveAuthentication;
import com.compactdrive.GoogleDrive.GoogleDriveUtils;
import com.compactdrive.GoogleDrive.GoogleDrive_SignOut;
import com.compactdrive.GoogleDrive.GoogleGetChildrenThread;
import com.compactdrive.OneDrive.GetCode_OneDrive;
import com.compactdrive.OneDrive.OneDriveAuthentication;
import com.compactdrive.OneDrive.OneDriveGetChildrenThread;
import com.compactdrive.OneDrive.OneDriveUtils;
import com.compactdrive.OneDrive.OneDrive_SignOut;
import com.compactdrive.R;

import java.net.URL;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

public class Central extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Controlling variables
    static boolean readyToExit = false;
    public static int driveId =0;

    //Container Variables
    public static Stack<String> parentStack = new Stack<String>();

    static DrawerLayout drawer;
    static String localDir = null;
    ProgressDialog prog;

    Toolbar toolbar;
    ImageView homeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        homeImage = (ImageView)findViewById(R.id.homeImageView);
        homeImage.setImageResource(R.drawable.home);

        prog = new ProgressDialog(this);
        prog.setMessage("Loading...");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        PackageManager m = getPackageManager();
        localDir = getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(localDir, 0);
            localDir = p.applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("Central", "Error Package name not found ", e);
        }
    }

    @Override
    public void onBackPressed() {

        ArrayList<CDFileObject> resultList=null;
        try {
            String parentId = parentStack.pop();
            if(parentId.startsWith("1")) {
                parentId=parentId.substring(1);
                resultList = GoogleDriveUtils.getChildrenByParent(parentId);
            }else if(parentId.startsWith("2")){
                parentId = parentId.substring(1);
                resultList = OneDriveUtils.getChildren(parentId);
            }else if (parentId.startsWith("3")){
                parentId = parentId.substring(1);
//                resultList = BoxUtils.getChildren(parentId);
            }else if (parentId.startsWith("4")){
                parentId = parentId.substring(1);
//                resultList = DropBoxUtils.getChildren(parentId);
            }else if (parentId.startsWith("5")){
                parentId = parentId.substring(1);
//                resultList = TempoBoxUtils.getChildren(parentId);
            }
            FileAdapter ap = new FileAdapter(Central.this, resultList);
            final ListView list = (ListView) findViewById(R.id.listView);
            list.setAdapter(ap);
        }
        catch(EmptyStackException ese){
            if(readyToExit) {
                System.exit(0);
            }else{
                readyToExit= true;
                Toast.makeText(Central.this,"Press again to exit",Toast.LENGTH_SHORT);
                openDrawer();
            }
        }
        catch (Exception e){
            Log.i("Central", e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.central, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.Log_out) {
            if(driveId == 1){
                Intent logout = new Intent(getApplicationContext(), GoogleDrive_SignOut.class);
                startActivity(logout);

            }else if(driveId == 2){
                Intent logout = new Intent(getApplicationContext(), OneDrive_SignOut.class);
                startActivity(logout);

            }else if(driveId == 3){

            }else if(driveId == 4){

            }else if(driveId == 5){

            }

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        drawer.closeDrawers();
        int id = item.getItemId();
        homeImage.setVisibility(View.INVISIBLE);

        //Clearing the screen
        FileAdapter ap = new FileAdapter(Central.this,new ArrayList<CDFileObject>());
        final ListView list = (ListView)findViewById(R.id.listView);
        list.setAdapter(ap);
        parentStack.clear();

        if (id == R.id.nav_googledrive) {
            driveId = 1;
            toolbar.setTitle("Google Drive");
            GoogleDriveAuthentication.readTokens();
            if(GoogleDriveAuthentication.aceToken == null || GoogleDriveAuthentication.aceToken.isEmpty()){
                Intent fetchCode = new Intent(getApplicationContext(), GetCode_GoogleDrive.class);
                startActivityForResult(fetchCode, driveId);
            }else{
                onActivityResult(driveId,0,null);
            }

        } else if (id == R.id.nav_onedrive) {
            driveId = 2;
            toolbar.setTitle("One Drive");
            OneDriveAuthentication.readTokens();
            if(OneDriveAuthentication.aceToken == null || OneDriveAuthentication.aceToken.isEmpty()) {
                Intent fetchCode = new Intent(getApplicationContext(), GetCode_OneDrive.class);
                startActivityForResult(fetchCode, driveId);
            } else{
                onActivityResult(driveId,0,null);
            }

        } else if (id == R.id.nav_tempobox) {
            driveId = 3;
            toolbar.setTitle("Tempo Box");

        } else if (id == R.id.nav_box) {
            driveId = 4;
            toolbar.setTitle("Box");

        } else if(id == R.id.nav_dropbox){
            driveId = 5;
            toolbar.setTitle("Drop Box");

        } else if (id == R.id.nav_about) {
            Intent about = new Intent(getApplicationContext(), About.class);
            startActivity(about);
        }

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//

        if(requestCode == 1){

            GoogleDriveAuthentication.populateTokens();
            GoogleGetChildrenThread childrenThread = new GoogleGetChildrenThread();
            childrenThread.start();

            try {
                childrenThread.join();
            }catch (InterruptedException Ie){
                Log.e("Central",Ie.getMessage());
            }

            //need to decide if it truely required
            while(true){
                if(childrenThread.getState()==Thread.State.TERMINATED){
                    break;
                }
            }

            ArrayList<CDFileObject> children = GoogleDriveUtils.getChildrenByParent("root");
            populateChildrenOnScreen(children);

        }else if(requestCode == 2){

            OneDriveAuthentication.populateTokens();
            ArrayList<CDFileObject> children = OneDriveUtils.getChildren("root");
            populateChildrenOnScreen(children);

        }else if(requestCode == 3){

        }else if(requestCode == 4){

        }else if(requestCode == 5){

        }
    }

    public void populateChildrenOnScreen(List<CDFileObject> children){
        if (children == null){
            children = new ArrayList<CDFileObject>();
        }
        try{
            FileAdapter ap = new FileAdapter(Central.this,children);
            final ListView list = (ListView)findViewById(R.id.listView);
            list.setAdapter(ap);
            prog.dismiss();

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    prog.show();
                    readyToExit = false;
                    CDFileObject temp = (CDFileObject) parent.getItemAtPosition(position);
                    if (temp.isFolder) {
                        try {
                            ArrayList<CDFileObject> adapter_result = null;
                            String docId = temp.getID();
                            if (docId.startsWith("1")) {
                                adapter_result = GoogleDriveUtils.getChildrenByParent(docId.substring(1));
                            } else if (docId.startsWith("2")) {
                                adapter_result = OneDriveUtils.getChildren(docId.substring(1));
                            } else if (docId.startsWith("3")) {

                            } else if (docId.startsWith("4")) {

                            } else if (docId.startsWith("5")) {

                            }
                            parentStack.push(driveId + temp.getParentId());
                            FileAdapter ap = new FileAdapter(Central.this, adapter_result);
                            ListView list = (ListView) findViewById(R.id.listView);
                            list.setAdapter(ap);
                        } catch (Exception e) {
                            Log.e("appcentral :", e.getMessage());
                        }
                    } else {
                        Intent t = new Intent(Central.this, fileDownload.class);
                        URL k = temp.getUrl();
                        t.putExtra("downURL", k.toString());
                        t.putExtra("MType", temp.getMimeType().toString());
                        t.putExtra("filename", temp.getTitle().toString());
                        t.putExtra("fileSize", temp.getSize());
                        t.putExtra("mimeType", temp.getMimeType());
                        startActivity(t);
                    }
                    prog.dismiss();
                }
            });

            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                            .getSystemService(LAYOUT_INFLATER_SERVICE);

                    View popupView = layoutInflater.inflate(R.layout.popup, null);
                    CDFileObject temp = (CDFileObject) parent.getItemAtPosition(position);
                    ImageButton close = (ImageButton) popupView.findViewById(R.id.popupClose);
                    TextView name = (TextView) popupView.findViewById(R.id.popup_name);
                    TextView dateOfCreation = (TextView) popupView.findViewById(R.id.dateofcreation);
                    TextView dateOfModification = (TextView) popupView.findViewById(R.id.dateofModification);
                    TextView owners = (TextView) popupView.findViewById(R.id.ownerslist);
                    try {
                        name.setText(":" + temp.getTitle());
                        dateOfCreation.setText(":" + temp.getDoc());
                        dateOfModification.setText(":" + temp.getDom());
                        owners.setText(":" + temp.getOwners());
                    } catch (Exception ne) {
                        //Do Nothing
                    }
                    final PopupWindow my_popup = new PopupWindow(popupView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
                    my_popup.setOutsideTouchable(true);
                    my_popup.setFocusable(true);
                    my_popup.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                    close.setVisibility(View.VISIBLE);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            my_popup.dismiss();
                        }
                    });
                    my_popup.setTouchInterceptor(new View.OnTouchListener() {

                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                                my_popup.dismiss();
                                return true;
                            }
                            return false;
                        }
                    });

                    return true;

                }
            });


        }
        catch (Exception e) {
            Log.e(" Home class", e.getMessage());
        }
    }

    public static void openDrawer(){
        drawer.openDrawer(Gravity.LEFT);
    }

    public static String getLocalDir() {
        return localDir;
    }
}
