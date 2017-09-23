package de.parcelbox.views;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.LinearLayout;

import de.parcelbox.R;

public class LaunchView extends LinearLayout {

    // listener for surrounding views
    private LaunchViewListener listener;

    // view elements
    private View wrapper;
    private Button launchBtn;

    public LaunchView(Context context) {
        this(context, null);
    }

    public LaunchView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // inflate the view
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.launch_view, this, true);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // get the loading and success dot
        wrapper = findViewById(R.id.launchWrapper);
        launchBtn = findViewById(R.id.launchStartBtn);
        launchBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                fadeoutView();
            }
        });
    }

    public void reset() {
        wrapper.setVisibility(VISIBLE);
        wrapper.setAlpha(1f);
        launchBtn.setTranslationY(0);
    }

    private void fadeoutView() {

        // create a fadeOut animation with interpolator and duration
        final Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(getResources().getInteger(R.integer.animation_speed_showhide));

        // create handler w/ runnable for animation end
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // make overlay gone
                wrapper.setVisibility(GONE);

                // notify listener
                notifyListener();

            }
        }, fadeOut.getDuration());

        // start animation
        wrapper.startAnimation(fadeOut);
        launchBtn.animate().translationY(launchBtn.getHeight() / 2f).setDuration(getResources().getInteger(R.integer.animation_speed_showhide));
    }

    /**
     * set the listener for event updates
     *
     * @param listener an event listener interface
     */
    public void setListener(LaunchViewListener listener) {
        this.listener = listener;
    }

    /**
     * send notification to the bound event listener
     */
    private void notifyListener() {
        if (listener != null) {
            listener.onStartClicked();
        }
    }

    /**
     * listener to notify when the user clicks on start
     */
    public interface LaunchViewListener {
        void onStartClicked();
    }
}
