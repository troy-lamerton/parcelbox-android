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
import de.parcelbox.views.CountdownView;
import de.parcelbox.views.LaunchView;
import de.parcelbox.views.LoadingView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        LaunchView.LaunchViewListener, CountdownView.CountdownViewListener {

    private CameraView mCameraView;

    private BoxManager boxManager;
    private PubnubManager pubnubManager;

    // view elements
    private LaunchView launchView;
    private CountdownView countdownView;
    private LoadingView loadingView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init managers
        boxManager = new BoxManager(this);
        pubnubManager = new PubnubManager();

        // create CameraView and bind to the layout
        mCameraView = new CameraView(this);
        mCameraView.setTextureView((TextureView) findViewById(R.id.camera_view));

        // get view elements
        launchView = (LaunchView) findViewById(R.id.launchView);
        launchView.setListener(this);

        countdownView = (CountdownView) findViewById(R.id.countdownView);
        countdownView.setListener(this);

        loadingView = (LoadingView) findViewById(R.id.loadingView);
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
                restart();
            }
        }

        @Override
        public void presence(PubNub pubnub, PNPresenceEventResult presence) {

        }
    };

    @Override
    public void onStartClicked() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                launchView.setVisibility(View.GONE);
                countdownView.setVisibility(View.VISIBLE);
                countdownView.startCountdown(MainActivity.this);
            }
        });

        mCameraView.takePicture();
    }

    @Override
    public void onCountdownExpired() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                countdownView.setVisibility(View.GONE);
                loadingView.setVisibility(View.VISIBLE);
                loadingView.startCountdown(MainActivity.this);
            }
        });
    }

    public void restart() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingView.fadeoutView();
                launchView.setVisibility(View.VISIBLE);
                launchView.reset();
            }
        });
    }
}
