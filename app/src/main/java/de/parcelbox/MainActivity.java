package de.parcelbox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.gson.JsonObject;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import de.parcelbox.manager.BoxManager;
import de.parcelbox.manager.PubnubManager;

public class MainActivity extends AppCompatActivity  {

    private CameraView mCameraView = null;

    private BoxManager boxManager;
    private PubnubManager pubnubManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init managers
        boxManager = new BoxManager(this);
        pubnubManager = new PubnubManager();

        // create CameraView and bind to the layout
        mCameraView = new CameraView(this);
        FrameLayout camera_view = (FrameLayout) findViewById(R.id.camera_view);
        camera_view.addView(mCameraView);

        // btn to close the application
        ImageButton imgClose = (ImageButton) findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boxManager.openDoor("Z", 1);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register PubnubManager channel and callback
        pubnubManager.subscribe("parcelbox");
        pubnubManager.start(subscribeCallback);
    }

    @Override
    protected void onPause() {
        pubnubManager.stop();

        super.onPause();
    }

    SubscribeCallback subscribeCallback = new SubscribeCallback() {
        @Override
        public void status(PubNub pubnub, PNStatus status) {

        }

        @Override
        public void message(PubNub pubnub, PNMessageResult message) {
            Log.d("MAIN", message.getMessage().toString());

            // try to get the box id out of the message and open the corresponding door
            JsonObject json = message.getMessage().getAsJsonObject();
            if(json != null && json.has("box-id")) {
                int boxId = json.get("box-id").getAsInt();
                boxManager.openDoor("Z", boxId);
            }
        }

        @Override
        public void presence(PubNub pubnub, PNPresenceEventResult presence) {

        }
    };
}
