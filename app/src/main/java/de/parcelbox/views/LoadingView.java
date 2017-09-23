package de.parcelbox.views;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.parcelbox.R;

public class LoadingView extends LinearLayout {

    // view elements
    private LinearLayout wrapper, percentageBar;
    private TextView percentageTxt, firstTxt, secondTxt, thirdTxt;
    private ImageView firstNoIv, firstYesIv, secondNoIv, secondYesIv, thirdNoIv, thirdYesIv;

    private int percentage = 0;
    private boolean doneLoading[];

    private Handler handler;
    private Runnable runnable;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // inflate the view
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.loading_view, this, true);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        wrapper = findViewById(R.id.loadingWrapper);

        // get the loading and success dot
        percentageBar = findViewById(R.id.loadingBar);
        percentageTxt = findViewById(R.id.loadingPercentTxt);

        firstTxt = findViewById(R.id.loadingFirstTxt);
        firstNoIv = findViewById(R.id.loadingFirstNoIv);
        firstYesIv = findViewById(R.id.loadingFirstYesIv);

        secondTxt = findViewById(R.id.loadingSecondTxt);
        secondNoIv = findViewById(R.id.loadingSecondNoIv);
        secondYesIv = findViewById(R.id.loadingSecondYesIv);

        thirdTxt = findViewById(R.id.loadingThirdTxt);
        thirdNoIv = findViewById(R.id.loadingThirdNoIv);
        thirdYesIv = findViewById(R.id.loadingThirdYesIv);
    }

    public void startCountdown(final Activity activity) {

        reset();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                updateUi(activity);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    private void updateUi(Activity activity) {
        percentage++;

        if (percentage > 99) {
            percentage = 99;
            handler.removeCallbacks(runnable);
            handler = null;
            return;
        }

        handler.postDelayed(runnable, 80 + (int) (Math.random() * 60));

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                percentageBar.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        percentage / 100f
                ));
                percentageTxt.setText(percentage + " %");

                // animate each row
                if (percentage > 20 && !doneLoading[0]) {
                    animateRow(firstTxt, firstNoIv, firstYesIv);
                    doneLoading[0] = true;
                }
                if (percentage > 40 && !doneLoading[1]) {
                    animateRow(secondTxt, secondNoIv, secondYesIv);
                    doneLoading[1] = true;
                }
                if (percentage > 65 && !doneLoading[2]) {
                    animateRow(thirdTxt, thirdNoIv, thirdYesIv);
                    doneLoading[2] = true;
                }
            }
        });
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
        percentage = 0;
        doneLoading = new boolean[]{false, false, false};

        wrapper.setAlpha(1f);
        wrapper.setVisibility(VISIBLE);

        firstTxt.setAlpha(0.6f);
        secondTxt.setAlpha(0.6f);
        thirdTxt.setAlpha(0.6f);

        firstNoIv.setAlpha(1f);
        firstYesIv.setAlpha(0f);
        secondNoIv.setAlpha(1f);
        secondYesIv.setAlpha(0f);
        thirdNoIv.setAlpha(1f);
        thirdYesIv.setAlpha(0f);
    }

    private void animateRow(TextView text, ImageView iconNo, ImageView iconYes) {
        if (text.getAlpha() == 0.6f) {
            text.animate().alpha(1f).setDuration(200);
            iconNo.animate().alpha(0f).setDuration(150);
            iconYes.animate().alpha(1f).setDuration(200);
        }
    }
}
