package de.parcelbox.manager;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;

import java.util.Arrays;

public class PubnubManager {

    private PubNub pubNub;
    private SubscribeCallback callback;

    public PubnubManager() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey("sub-c-9cbba3d6-a053-11e7-96f6-d664df0bd9f6");
        pnConfiguration.setSecure(true);
        pubNub = new PubNub(pnConfiguration);
    }

    public void subscribe(String channel) {
        pubNub.subscribe().channels(Arrays.asList(channel)).execute();
    }

    public void start(SubscribeCallback subscribeCallback) {
        callback = subscribeCallback;
        pubNub.addListener(subscribeCallback);
    }

    public void stop() {
        if (callback != null) {
            pubNub.removeListener(callback);
            callback = null;
        }
    }
}
