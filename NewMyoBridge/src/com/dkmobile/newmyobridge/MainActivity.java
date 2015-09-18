package com.dkmobile.newmyobridge;

import com.dkmobile.newmyobridge.BackgroundService;
import com.dkmobile.newmyobridge.BackgroundService.LocalBinder;
import com.thalmic.myo.scanner.ScanActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnLongClickListener {

    BackgroundService mBackgroundService = null;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("MYOSERVICE", "onServiceConnected()");
            mBackgroundService = ((LocalBinder) service).MyoService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("MYOSERVICE", "onServiceDisConnected()");
        }
    };
    Intent serviceIntent = new Intent("com.dkmobile.newmyobridge.BackgroundService");

    private TextView statusTextView;
    private TextView armTextView;

    public SharedPreferences mPref;
    private PackageManager manager;

    ImageView dtview;
    ImageView fstview;
    ImageView woview;
    ImageView wiview;
    ImageView spview;

    Button mainDTbutton;
    Button mainFSTbutton;
    Button mainWObutton;
    Button mainWIbutton;
    Button mainFSbutton;

    Intent dt_selected_app = new Intent();
    String dt_app_name = null;
    Intent fst_selected_app = new Intent();
    String fst_app_name = null;
    Intent wo_selected_app = new Intent();
    String wo_app_name = null;
    Intent wi_selected_app = new Intent();
    String wi_app_name = null;
    Intent fs_selected_app = new Intent();
    String fs_app_name = null;

    // long click
    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
        case R.id.main_DT_btn: {
            Intent i = new Intent();
            i.setClass(this, AppListActivity.class);
            startActivityForResult(i, 0);
            return true;
        }
        case R.id.main_FST_btn: {
            Intent i = new Intent();
            i.setClass(this, AppListActivity.class);
            startActivityForResult(i, 1);
            return true;
        }
        case R.id.main_FS_btn: {
            Intent i = new Intent();
            i.setClass(this, AppListActivity.class);
            startActivityForResult(i, 2);
            return true;
        }
        case R.id.main_WO_btn: {
            Intent i = new Intent();
            i.setClass(this, AppListActivity.class);
            startActivityForResult(i, 3);
            return true;
        }
        case R.id.main_WI_btn: {
            Intent i = new Intent();
            i.setClass(this, AppListActivity.class);
            startActivityForResult(i, 4);
            return true;
        }
        }
        return false;
    }// long click end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // image views linked
        fstview = (ImageView) findViewById(R.id.imageFST);
        dtview = (ImageView) findViewById(R.id.imageDT);
        woview = (ImageView) findViewById(R.id.imageWO);
        wiview = (ImageView) findViewById(R.id.imageWI);
        spview = (ImageView) findViewById(R.id.imageSP);

        // textView linked
        statusTextView = (TextView) findViewById(R.id.main_status);
        armTextView = (TextView) findViewById(R.id.main_armstatus);

        // Buttons linked
        mainDTbutton = (Button) findViewById(R.id.main_DT_btn);
        mainDTbutton.setOnLongClickListener(this);
        mainFSTbutton = (Button) findViewById(R.id.main_FST_btn);
        mainFSTbutton.setOnLongClickListener(this);
        mainWObutton = (Button) findViewById(R.id.main_WO_btn);
        mainWObutton.setOnLongClickListener(this);
        mainWIbutton = (Button) findViewById(R.id.main_WI_btn);
        mainWIbutton.setOnLongClickListener(this);
        mainFSbutton = (Button) findViewById(R.id.main_FS_btn);
        mainFSbutton.setOnLongClickListener(this);

        mPref = getSharedPreferences("SaveState", Context.MODE_PRIVATE);

        // call back button texts from shared Preferences
        mainDTbutton.setText(mPref.getString("dt_appname", "App Define"));
        dt_app_name = mPref.getString("dt_appname", null);
        mainFSTbutton.setText(mPref.getString("fst_appname", "App Define"));
        fst_app_name = mPref.getString("fst_appname", null);
        mainWIbutton.setText(mPref.getString("wi_appname", "App Define"));
        wi_app_name = mPref.getString("wi_appname", null);
        mainWObutton.setText(mPref.getString("wo_appname", "App Define"));
        wo_app_name = mPref.getString("wo_appname", null);
        mainFSbutton.setText(mPref.getString("fs_appname", "App Define"));
        fs_app_name = mPref.getString("fs_appname", null);

        manager = getPackageManager();

        // call back saved apps
        dt_selected_app = manager.getLaunchIntentForPackage(mPref.getString("dt_activity", null));
        fst_selected_app = manager.getLaunchIntentForPackage(mPref.getString("fst_activity", null));
        wi_selected_app = manager.getLaunchIntentForPackage(mPref.getString("wi_activity", null));
        wo_selected_app = manager.getLaunchIntentForPackage(mPref.getString("wo_activity", null));
        fs_selected_app = manager.getLaunchIntentForPackage(mPref.getString("fs_activity", null));

        serviceIntent.setPackage("com.dkmobile.newmyobridge");

        serviceIntent.putExtra("test", "This is test !!!");
        if (mPref.getString("dt_activity", null) != null) {
            serviceIntent.putExtra("dt_app_v", true);
            serviceIntent.putExtra("dt_app", mPref.getString("dt_activity", null));
        } else {
            serviceIntent.putExtra("dt_app_v", false);
        }
        if (mPref.getString("fst_activity", null) != null) {
            serviceIntent.putExtra("fst_app_v", true);
            serviceIntent.putExtra("fst_app", mPref.getString("fst_activity", null));
        } else {
            serviceIntent.putExtra("fst_app_v", false);
        }
        if (mPref.getString("fs_activity", null) != null) {
            serviceIntent.putExtra("fs_app_v", true);
            serviceIntent.putExtra("fs_app", mPref.getString("fs_activity", null));
        } else {
            serviceIntent.putExtra("fs_app_v", false);
        }
        if (mPref.getString("wi_activity", null) != null) {
            serviceIntent.putExtra("wi_app_v", true);
            serviceIntent.putExtra("wi_app", mPref.getString("wi_activity", null));
        } else {
            serviceIntent.putExtra("wi_app_v", false);
        }
        if (mPref.getString("wo_activity", null) != null) {
            serviceIntent.putExtra("wo_app_v", true);
            serviceIntent.putExtra("wo_app", mPref.getString("wo_activity", null));
        } else {
            serviceIntent.putExtra("wo_app_v", false);
        }

        // startService(serviceIntent);
        bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
        startService(serviceIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (R.id.action_scan == id) {
            onScanActionSelected();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onScanActionSelected() {
        // Launch the ScanActivity to scan for Myos to connect to.
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {

        mPref = getSharedPreferences("SaveState", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = mPref.edit();

        String dt_activity = null;
        String fst_activity = null;
        String wi_activity = null;
        String wo_activity = null;
        String fs_activity = null;

        manager = getPackageManager();
        switch (requestCode) {
        case 0: { // double tap click
            if (resultCode == RESULT_OK) {
                dt_app_name = i.getStringExtra("App_name");

                if (dt_app_name == null) {
                    mainDTbutton.setText("App Define");
                    edit.remove("dt_appname");
                    edit.remove("dt_activity");
                    edit.apply();
                    serviceIntent.putExtra("dt_app_v", false);
                } else {
                    dt_activity = i.getStringExtra("App_Activity");
                    dt_selected_app = manager.getLaunchIntentForPackage(dt_activity);
                    mainDTbutton.setText(dt_app_name);
                    edit.putString("dt_appname", dt_app_name);
                    edit.putString("dt_activity", dt_activity);
                    edit.apply();

                    serviceIntent.putExtra("dt_app_v", true);
                    serviceIntent.putExtra("dt_app", i.getStringExtra("App_Activity"));
                }
                startService(serviceIntent);
            }
            break;
        }
        case 1: { // fist click
            if (resultCode == RESULT_OK) {
                fst_app_name = i.getStringExtra("App_name");

                if (fst_app_name == null) {
                    mainFSTbutton.setText("App Define");
                    edit.remove("fst_appname");
                    edit.remove("fst_activity");
                    edit.apply();

                    serviceIntent.putExtra("fst_app_v", false);
                } else {
                    fst_activity = i.getStringExtra("App_Activity");
                    fst_selected_app = manager.getLaunchIntentForPackage(fst_activity);
                    mainFSTbutton.setText(fst_app_name);
                    edit.putString("fst_appname", fst_app_name);
                    edit.putString("fst_activity", fst_activity);
                    edit.apply();

                    serviceIntent.putExtra("fst_app_v", true);
                    serviceIntent.putExtra("fst_app", i.getStringExtra("App_Activity"));
                }

                startService(serviceIntent);
            }
            break;
        }
        case 2: {// finger spread click
            if (resultCode == RESULT_OK) {
                fs_app_name = i.getStringExtra("App_name");

                if (fs_app_name == null) {
                    mainFSbutton.setText("App Define");
                    edit.remove("fs_appname");
                    edit.remove("fs_activity");
                    edit.apply();
                    serviceIntent.putExtra("fs_app_v", false);
                } else {
                    fs_activity = i.getStringExtra("App_Activity");
                    fs_selected_app = manager.getLaunchIntentForPackage(fs_activity);
                    mainFSbutton.setText(fs_app_name);
                    edit.putString("fs_appname", fs_app_name);
                    edit.putString("fs_activity", fs_activity);
                    edit.apply();

                    serviceIntent.putExtra("fs_app_v", true);
                    serviceIntent.putExtra("fs_app", i.getStringExtra("App_Activity"));
                }
                startService(serviceIntent);
            }
            break;
        }
        case 3: { // wave out click
            if (resultCode == RESULT_OK) {
                wo_app_name = i.getStringExtra("App_name");

                if (wo_app_name == null) {
                    mainWObutton.setText("App Define");
                    edit.remove("wo_appname");
                    edit.remove("wo_activity");
                    edit.apply();
                    serviceIntent.putExtra("wo_app_v", false);
                } else {
                    wo_activity = i.getStringExtra("App_Activity");
                    wo_selected_app = manager.getLaunchIntentForPackage(wo_activity);
                    mainWObutton.setText(wo_app_name);
                    edit.putString("wo_appname", wo_app_name);
                    edit.putString("wo_activity", wo_activity);
                    edit.apply();

                    serviceIntent.putExtra("wo_app_v", true);
                    serviceIntent.putExtra("wo_app", i.getStringExtra("App_Activity"));
                }
                startService(serviceIntent);
            }
            break;
        }
        case 4: { // wave in click
            if (resultCode == RESULT_OK) {

                wi_app_name = i.getStringExtra("App_name");

                if (wi_app_name == null) {
                    mainWIbutton.setText("App Define");
                    edit.remove("wi_appname");
                    edit.remove("wi_activity");
                    edit.apply();
                    serviceIntent.putExtra("wi_app_v", false);
                } else {
                    wi_activity = i.getStringExtra("App_Activity");
                    wi_selected_app = manager.getLaunchIntentForPackage(wi_activity);
                    mainWIbutton.setText(wi_app_name);
                    edit.putString("wi_appname", wi_app_name);
                    edit.putString("wi_activity", wi_activity);
                    edit.apply();

                    serviceIntent.putExtra("wi_app_v", true);
                    serviceIntent.putExtra("wi_app", i.getStringExtra("App_Activity"));
                }
                startService(serviceIntent);
            }
            break;
        }
        }// end of switch
    }// end of onActivity result

    // onClick Functions
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.main_DT_btn: {
            if (dt_app_name == null) {
                Intent i = new Intent();
                i.setClass(this, AppListActivity.class);
                startActivityForResult(i, 0);
            } else {
                startActivity(dt_selected_app);
            }
            break;
        }
        case R.id.main_FST_btn:
            if (fst_app_name == null) {
                Intent i = new Intent();
                i.setClass(this, AppListActivity.class);
                startActivityForResult(i, 1);
            } else {
                startActivity(fst_selected_app);
            }
            break;
        case R.id.main_WI_btn:
            if (wi_app_name == null) {
                Intent i = new Intent();
                i.setClass(this, AppListActivity.class);
                startActivityForResult(i, 4);
            } else {
                startActivity(wi_selected_app);
            }
            break;
        case R.id.main_WO_btn:
            if (fs_app_name == null) {
                Intent i = new Intent();
                i.setClass(this, AppListActivity.class);
                startActivityForResult(i, 3);
            } else {
                startActivity(wo_selected_app);
            }
            break;
        case R.id.main_FS_btn:
            if (fs_app_name == null) {
                Intent i = new Intent();
                i.setClass(this, AppListActivity.class);
                startActivityForResult(i, 2);
            } else {
                startActivity(fs_selected_app);
            }
            break;
        }
    }

}
