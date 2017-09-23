package de.parcelbox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "PB-MAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.hal.iocontroller.querydata");
        filter.addAction("android.intent.action.hal.iocontroller.queryAllData");
        this.registerReceiver(new DoorReceiver(), filter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        queryDoor("Z", 1);

        openDoor("Z", 2);


        setLampStatus(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                queryAllOpenDoors("Z", 7);
            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                queryAllOpenDoors("Z", 7);

                setLampStatus(false);
            }
        }, 5000);
    }

    private void openDoor(String locker, int doorId) {
        String doorStringId = getDoorIdString(locker, doorId);
        if (doorStringId == null) return;

        Intent intent = new Intent("android.intent.action.hal.iocontroller.open");
        intent.putExtra("boxid", doorStringId);
        sendBroadcast(intent);
    }

    private void queryDoor(String locker, int doorId) {
        String doorStringId = getDoorIdString(locker, doorId);
        if (doorStringId == null) return;

        Intent intent = new Intent("android.intent.action.hal.iocontroller.query");
        intent.putExtra("boxid", doorStringId);
        sendBroadcast(intent);
    }

    private void queryAllOpenDoors(String locker, int doorCount) {
        String doorStringId = getDoorIdString(locker, doorCount);
        if (doorStringId == null) return;

        Log.d(TAG, "queryAllOpenDoors");
        Intent intent = new Intent("android.intent.action.hal.iocontroller.queryAll");
        intent.putExtra("boxCount", doorStringId);
        sendBroadcast(intent);
    }

    private void setLampStatus(boolean isOn) {
        Log.d(TAG, "setLampStatus: " + isOn);
        Intent intent = new Intent("android.intent.action.hal.lamp.main.onoff");
        intent.putExtra("onoff", isOn ? 1 : 0);
        sendBroadcast(intent);

    }

    private String getDoorIdString(String locker, int doorId) {
        if (doorId < 0 && doorId > 99) return null;
        return locker + ((doorId < 10) ? "0" + doorId : doorId);
    }

    private class DoorReceiver extends BroadcastReceiver {

        private String TAG = "PB-DOOR";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "intent is: " + intent.getAction());

            // query specific box
            if (intent.getAction().equals("android.intent.action.hal.iocontroller.querydata")) {
                String boxId = intent.getExtras().getString("boxid");
                boolean isOpen = intent.getExtras().getBoolean("isopened");
                Log.d(TAG, "box " + boxId + " is open: " + isOpen);
            }
            // query open boxes
            else if (intent.getAction().equals("android.intent.action.hal.iocontroller.queryAllData")) {
                String[] openBoxes = intent.getExtras().getStringArray("openedBoxes");
                if(openBoxes != null && openBoxes.length > 0) {
                    for (String openBoxId:openBoxes) {
                        Log.d(TAG, "box open: "+openBoxId);
                    }
                } else {
                    Log.d(TAG, "all boxes closed");
                }
            }

        }
    }
}
