package de.parcelbox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;
import android.view.View;

import com.google.gson.JsonObject;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import de.parcelbox.manager.BoxManager;
import de.parcelbox.manager.PubnubManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CameraView mCameraView;

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
        TextureView camera_view = (TextureView) findViewById(R.id.camera_view);
        mCameraView.setTextureView(camera_view);
/*
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        matrix.postTranslate(500, 0);
        mTextureView.setTransform(matrix);
*/
        // camera_view.setScaleX(-1);

        // bind buttons with OCL
        findViewById(R.id.takePicture).setOnClickListener(this);
        findViewById(R.id.openDoor).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        pubnubManager.subscribe("parcelbox");
        pubnubManager.start(subscribeCallback);
    }

    @Override
    protected void onPause() {
        pubnubManager.stop();
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.takePicture:
                mCameraView.takePicture();
                break;

            case R.id.openDoor:
                boxManager.openDoor("Z", 1);
                break;
        }
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
            if (json != null && json.has("box-id")) {
                int boxId = json.get("box-id").getAsInt();
                boxManager.openDoor("Z", boxId);
            }
        }

        @Override
        public void presence(PubNub pubnub, PNPresenceEventResult presence) {

        }
    };
}
