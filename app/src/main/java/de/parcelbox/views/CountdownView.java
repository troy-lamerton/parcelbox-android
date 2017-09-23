package de.parcelbox.views;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import de.parcelbox.R;

public class CountdownView extends LinearLayout {

    // listener for surrounding views
    private CountdownViewListener listener;

    // view elements
    private View flashView;
    private TextView countdownTxt;
    private int remainingTicks;

    private Timer timer;

    public CountdownView(Context context) {
        this(context, null);
    }

    public CountdownView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // inflate the view
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.countdown_view, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // get the loading and success dot
        countdownTxt = findViewById(R.id.countdownTxt);
        flashView = findViewById(R.id.countdownFlash);
    }

    public void startCountdown(final Activity activity) {
        remainingTicks = 3;
        countdownTxt.setText(String.valueOf(remainingTicks));

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateUi(activity);
            }
        }, 1000, 1000);
    }

    private void updateUi(Activity activity) {
        remainingTicks--;
        if (remainingTicks < 1) {
            showFlash(activity);
            timer.cancel();
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                countdownTxt.setText(String.valueOf(remainingTicks));
            }
        });
    }

    private void showFlash(Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                flashView.setVisibility(VISIBLE);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        flashView.setVisibility(GONE);
                        notifyListener();
                    }
                }, 100);
            }
        });
    }

    /**
     * set the listener for event updates
     *
     * @param listener an event listener interface
     */
    public void setListener(CountdownViewListener listener) {
        this.listener = listener;
    }

    /**
     * send notification to the bound event listener
     */
    private void notifyListener() {
        if (listener != null) {
            listener.onCountdownExpired();
        }
    }

    /**
     * listener to notify when the user clicks on start
     */
    public interface CountdownViewListener {
        void onCountdownExpired();
    }
}
