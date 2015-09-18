package com.dkmobile.newmyobridge;


import com.dkmobile.newmyobridge.BackgroundService.LocalBinder;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class BackgroundService extends Service {

    Intent dt_selected_app = new Intent();
    boolean dt_app_v;
    Intent fst_selected_app = new Intent();
    boolean fst_app_v;
    Intent wo_selected_app = new Intent();
    boolean wo_app_v;
    Intent wi_selected_app = new Intent();
    boolean wi_app_v;
    Intent fs_selected_app = new Intent();
    boolean fs_app_v;

    private static final String PREF_MAC_ADDRESS = "PREF_MAC_ADDRESS";

    public SharedPreferences mPrefs;
    private Hub mHub;
    private Toast mToast;

    private PackageManager manager;
    
    BroadcastReceiver mExitReceiver=null;

    // Detach from the currently attached Myo, if any, and attach to a new
    // one.
    public void attachToNewMyo() {
        // Detach from the previously attached Myo, if it exists.
        mHub.detach(mPrefs.getString(PREF_MAC_ADDRESS, ""));

        // Clear the saved Myo mac address.
        mPrefs.edit().putString(PREF_MAC_ADDRESS, "").apply();

        // Begin looking for an adjacent Myo to attach to.
        mHub.attachToAdjacentMyo();
    }

    // Classes that inherit from AbstractDeviceListener can be used to receive
    // events from Myo devices.
    // If you do not override an event, the default behavior is to do nothing.
    private DeviceListener mListener = new AbstractDeviceListener() {
        @Override
        public void onConnect(Myo myo, long timestamp) {
            showToast("connected");
        }

        // onAttach() is called whenever a Myo has been attached.
        @Override
        public void onAttach(Myo myo, long timestamp) {
            // Store the MAC address of the attached Myo so we can automatically
            // attach to it
            // the next time the app starts.
            mPrefs.edit().putString(PREF_MAC_ADDRESS, myo.getMacAddress()).apply();
        }

        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            showToast("disconnected");
        }

        // onPose() is called whenever the Myo detects that the person wearing
        // it has changed their pose, for example,
        // making a fist, or not making a fist anymore.
        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            // Show the name of the pose in a toast.
            showToast(pose.toString());

            // Handle the cases of the Pose enumeration, and change the text of
            // the text view
            // based on the pose we receive.

            switch (pose) {
            case UNKNOWN:

                break;
            case REST:

                break;
            case DOUBLE_TAP:
                break;
            case FIST:
                if (fst_app_v) {
                    startActivity(fst_selected_app);
                } else {
                    showToast("no App");
                }
                break;
            case WAVE_IN:
                if (wi_app_v) {
                    startActivity(wi_selected_app);
                } else {
                    showToast("no App");
                }
                break;
            case WAVE_OUT:
                if (wo_app_v = true) {
                    startActivity(wo_selected_app);
                } else {
                    showToast("no App");
                }
                break;
            case FINGERS_SPREAD:
                if (fs_app_v) {
                    startActivity(fs_selected_app);
                } else {
                    showToast("no App");
                }
                break;
            }
            if (pose != Pose.UNKNOWN && pose != Pose.REST) {
                // Tell the Myo to stay unlocked until told otherwise. We do
                // that here so you can
                // hold the poses without the Myo becoming locked.
                myo.unlock(Myo.UnlockType.HOLD);

                // Notify the Myo that the pose has resulted in an action, in
                // this case changing
                // the text on the screen. The Myo will vibrate.
                myo.notifyUserAction();
            } else {
                // Tell the Myo to stay unlocked only for a short period. This
                // allows the Myo to
                // stay unlocked while poses are being performed, but lock after
                // inactivity.
                myo.unlock(Myo.UnlockType.TIMED);
            }
        }
    };
    
    

    @Override
    public void onCreate() {
        super.onCreate();
        // First, we initialize the Hub singleton with an application
        // identifier.
        mHub = Hub.getInstance();
        if (!mHub.init(this, getPackageName())) {
            Log.e("MYOSERVICE", "Could not initialize the Hub.");
            stopSelf();
            return;
        }

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Register for DeviceListener callbacks.
        mHub.addListener(mListener);

        // If there is no connected Myo, try to attach to one.
        if (mHub.getConnectedDevices().isEmpty()) {
            String myoAddress = mPrefs.getString(PREF_MAC_ADDRESS, "");

            // If we have a saved Myo MAC address then connect to it, otherwise
            // look for one nearby.
            if (TextUtils.isEmpty(myoAddress)) {
                mHub.attachToAdjacentMyo();
            } else {
                mHub.attachByMacAddress(myoAddress);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("MYOSERVICE", "onStartCommand()");
        Intent exitIntent = new Intent("com.dkmobile.newmyobridge.exitactivity");
        exitIntent.setPackage("com.dkmobile.newmyobridge");
       
        //PendingIntent pIntent = PendingIntent.getActivity(this, 0, exitIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pIntent_= PendingIntent.getBroadcast(this, 0, exitIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        // Notification to build
        Notification noti = new Notification.Builder(this).setContentTitle("Myo Bridge")
                .setContentText("Running Myo Bridge").setSmallIcon(R.drawable.myo_icon)
                .build();
        
        
        RemoteViews contentView =new RemoteViews(getPackageName(), R.layout.noti_exit);
        contentView.setOnClickPendingIntent(R.id.noti_exit, pIntent_);
        contentView.setTextViewText(R.id.noti_name, "Myo Bridge");
        contentView.setTextViewText(R.id.noti_label, "Running Myo Bridge");
        contentView.setImageViewResource(R.id.noti_icon, R.drawable.myo_icon);
        noti.contentView=contentView;
        
        startForeground(1234, noti);
        // Notification end        
        
        //Braodcast receiver
        IntentFilter intentFilter= new IntentFilter();
        intentFilter.addAction("com.dkmobile.newmyobridge.exitactivity");
        //동적리시버 등록
        mExitReceiver=new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("NOTI", "Exitbutton");
                stopSelf();
                android.os.Process.killProcess(android.os.Process.myPid());
               }  
        };
        
        registerReceiver(mExitReceiver, intentFilter);
        
        manager = getPackageManager();
        if (intent.getExtras().getBoolean("wo_app_v")) {
            wo_selected_app = manager.getLaunchIntentForPackage(intent.getExtras().getString("wo_app"));
            wo_app_v = true;
            Log.e("MYOSERVICE", intent.getExtras().getString("wo_app"));
        } else {
            wo_app_v = false;
        }
        if (intent.getExtras().getBoolean("wi_app_v")) {
            wi_selected_app = manager.getLaunchIntentForPackage(intent.getExtras().getString("wi_app"));
            wi_app_v = true;
            Log.e("MYOSERVICE", intent.getExtras().getString("wi_app"));
        } else {
            wi_app_v = false;
        }
        if (intent.getExtras().getBoolean("dt_app_v")) {
            dt_selected_app = manager.getLaunchIntentForPackage(intent.getExtras().getString("dt_app"));
            dt_app_v = true;
            Log.e("MYOSERVICE", intent.getExtras().getString("dt_app"));
        } else {
            dt_app_v = false;
        }
        if (intent.getExtras().getBoolean("fst_app_v")) {
            fst_selected_app = manager.getLaunchIntentForPackage(intent.getExtras().getString("fst_app"));
            fst_app_v = true;
            Log.e("MYOSERVICE", intent.getExtras().getString("fst_app"));
        } else {
            fst_app_v = false;
        }
        if (intent.getExtras().getBoolean("fs_app_v")) {
            fs_selected_app = manager.getLaunchIntentForPackage(intent.getExtras().getString("fs_app"));
            fs_app_v = true;
            Log.e("MYOSERVICE", intent.getExtras().getString("fs_app"));
        } else {
            fs_app_v = false;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // We don't want any callbacks when the Service is gone, so unregister
        // the listener.
        
        mHub.getInstance().removeListener(mListener);
        mHub.getInstance().shutdown();
        unregisterReceiver(mExitReceiver);
    }

    private void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }

    public class LocalBinder extends Binder {

        BackgroundService MyoService() {
            return BackgroundService.this;
        }
    };

    private final Binder mBinder = new LocalBinder();

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("MYOSERVICE", "onUnbind()");
        return super.onUnbind(intent);
    }

    @Override
    public Binder onBind(Intent intent) {
        manager = getPackageManager();

        if (intent.getExtras().getBoolean("wo_app_v")) {
            wo_selected_app = manager.getLaunchIntentForPackage(intent.getExtras().getString("wo_app"));
            wo_app_v = true;
            Log.e("MYOSERVICE", intent.getExtras().getString("wo_app"));
        } else {
            wo_app_v = false;
        }
        if (intent.getExtras().getBoolean("wi_app_v")) {
            wi_selected_app = manager.getLaunchIntentForPackage(intent.getExtras().getString("wi_app"));
            wi_app_v = true;
            Log.e("MYOSERVICE", intent.getExtras().getString("wi_app"));
        } else {
            wi_app_v = false;
        }
        if (intent.getExtras().getBoolean("dt_app_v")) {
            dt_selected_app = manager.getLaunchIntentForPackage(intent.getExtras().getString("dt_app"));
            dt_app_v = true;
            Log.e("MYOSERVICE", intent.getExtras().getString("dt_app"));
        } else {
            dt_app_v = false;
        }
        if (intent.getExtras().getBoolean("fst_app_v")) {
            fst_selected_app = manager.getLaunchIntentForPackage(intent.getExtras().getString("fst_app"));
            fst_app_v = true;
            Log.e("MYOSERVICE", intent.getExtras().getString("fst_app"));
        } else {
            fst_app_v = false;
        }
        if (intent.getExtras().getBoolean("fs_app_v")) {
            fs_selected_app = manager.getLaunchIntentForPackage(intent.getExtras().getString("fs_app"));
            fs_app_v = true;
            Log.e("MYOSERVICE", intent.getExtras().getString("fs_app"));
        } else {
            fs_app_v = false;
        }
        return mBinder;
    }

}