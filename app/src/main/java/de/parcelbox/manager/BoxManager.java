package de.parcelbox.manager;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BoxManager {

    private Context context;

    public BoxManager(Context context) {
        this.context = context;
    }

    public void openDoor(String locker, int doorId) {
        String doorStringId = getDoorIdString(locker, doorId);
        if (doorStringId == null) return;

        Intent intent = new Intent("android.intent.action.hal.iocontroller.open");
        intent.putExtra("boxid", doorStringId);
        sendBroadcast(intent);
    }

    public void queryDoor(String locker, int doorId) {
        String doorStringId = getDoorIdString(locker, doorId);
        if (doorStringId == null) return;

        Intent intent = new Intent("android.intent.action.hal.iocontroller.query");
        intent.putExtra("boxid", doorStringId);
        sendBroadcast(intent);
    }

    public void queryAllOpenDoors(String locker, int doorCount) {
        String doorStringId = getDoorIdString(locker, doorCount);
        if (doorStringId == null) return;

        Intent intent = new Intent("android.intent.action.hal.iocontroller.queryAll");
        intent.putExtra("boxCount", doorStringId);
        sendBroadcast(intent);
    }

    public void setLampStatus(boolean isOn) {
        Intent intent = new Intent("android.intent.action.hal.lamp.main.onoff");
        intent.putExtra("onoff", isOn ? 1 : 0);
        sendBroadcast(intent);

    }

    private String getDoorIdString(String locker, int doorId) {
        if (doorId < 0 && doorId > 99) return null;
        return locker + ((doorId < 10) ? "0" + doorId : doorId);
    }

    private void sendBroadcast(Intent intent) {
        context.sendBroadcast(intent);
    }
}
