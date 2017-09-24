package de.parcelbox.views;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import de.parcelbox.R;

public class ResultView extends LinearLayout {

    // view elements
    private FrameLayout wrapper;
    private ImageView resultIv;

    private Handler handler;
    private Runnable runnable;

    private ResultViewListener listener;

    public ResultView(Context context) {
        this(context, null);
    }

    public ResultView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // inflate the view
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.result_view, this, true);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        wrapper = findViewById(R.id.resultWrapper);
        resultIv = findViewById(R.id.resultIv);
    }

    public void init(String imageUrl, final Activity activity) {

        reset();

        Picasso.with(activity)
                .load(imageUrl)
                .fit()
                .centerCrop()
                .into(resultIv);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                fadeoutView();
                notifyListener();
            }
        };
        handler.postDelayed(runnable, 10000);
    }


    public void fadeoutView() {

        // create a fadeOut animation with interpolator and duration
        final Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(getResources().getInteger(R.integer.animation_speed_showhide));

        // create handler w/ runnable for animation end
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                wrapper.setVisibility(GONE);
            }
        }, fadeOut.getDuration());

        // start animation
        wrapper.startAnimation(fadeOut);
    }

    public void reset() {
        wrapper.setAlpha(1f);
        wrapper.setVisibility(VISIBLE);
    }

    /**
     * set the listener for event updates
     *
     * @param listener an event listener interface
     */
    public void setListener(ResultViewListener listener) {
        this.listener = listener;
    }

    /**
     * send notification to the bound event listener
     */
    private void notifyListener() {
        if (listener != null) {
            listener.onResultExpired();
        }
    }

    /**
     * listener to notify when the user clicks on start
     */
    public interface ResultViewListener {
        void onResultExpired();
    }
}
