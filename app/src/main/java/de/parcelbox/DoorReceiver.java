package de.parcelbox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DoorReceiver extends BroadcastReceiver {

    private static String TAG = "PB-DOOR";

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
