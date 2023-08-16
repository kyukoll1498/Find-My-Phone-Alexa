package com.widget.custom_seekbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

/**
 * @author Minh
 * @version 1.0
 * @since 11/3/2017.
 */

public class DynamicSeekBarView extends LinearLayout implements SeekBar.OnSeekBarChangeListener {
    private CustomSeekBar seekBar;
    private View arrow;
    private TextView tvInfo;
    private LinearLayout llInfo;
    private String seekBarTextInfo = "";
    private SeekBar.OnSeekBarChangeListener _seekBarChangeListener;
    private int presetProgress = -1;

    public DynamicSeekBarView(Context context) {
        super(context);
        init(context, null);
    }

    public DynamicSeekBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DynamicSeekBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @SuppressLint("MissingInflatedId")
    private void init(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dynamic_seek_bar_view, this, false);
        seekBar = view.findViewById(R.id.seek_bar);
        arrow = view.findViewById(R.id.arrow);
        tvInfo = view.findViewById(R.id.tvInfo);
        llInfo = view.findViewById(R.id.llInfo);
        seekBar.setOnSeekBarChangeListener(this);
        tvInfo.setText("" + seekBar.getProgress());

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DynamicSeekBarView, 0, 0);
            int thumbSize = a.getDimensionPixelSize(R.styleable.DynamicSeekBarView_dsbv_thumbSize,
                    getResources().getDimensionPixelSize(R.dimen.default_thumb_size));
            int thumbDrawable = a.getResourceId(R.styleable.DynamicSeekBarView_dsbv_thumbDrawable, 0);
            int progressDrawable = a.getResourceId(R.styleable.DynamicSeekBarView_dsbv_progressDrawable, 0);
            int progressColor = a.getResourceId(R.styleable.DynamicSeekBarView_dsbv_progressColor, 0);
            final int progressBackgroundColor = a.getResourceId(R.styleable.DynamicSeekBarView_dsbv_progressBackgroundColor, 0);
            int infoSize = a.getDimensionPixelSize(R.styleable.DynamicSeekBarView_dsbv_infoSize, 0);
            int infoTextSize = a.getDimensionPixelSize(R.styleable.DynamicSeekBarView_dsbv_infoTextSize, 0);
            int infoTextColor = a.getResourceId(R.styleable.DynamicSeekBarView_dsbv_infoTextColor, 0);
            int infoRadius = a.getInt(R.styleable.DynamicSeekBarView_dsbv_infoRadius, 0);
            int infoBackgroundColor = a.getResourceId(R.styleable.DynamicSeekBarView_dsbv_infoBackgroundColor, 0);
            int maxValueSeekBar = a.getInt(R.styleable.DynamicSeekBarView_dsbv_max, 100);
            final int[] progressValueSeekBar = {a.getInt(R.styleable.DynamicSeekBarView_dsbv_progress, 0)};
            boolean isHideInfo = a.getBoolean(R.styleable.DynamicSeekBarView_dsbv_isHideInfo, false);
            String initInfoString = a.getString(R.styleable.DynamicSeekBarView_dsbv_infoInitText);

            if (thumbDrawable != 0) {
                seekBar.setThumb(thumbDrawable, thumbSize);
            }


            PorterDuff.Mode mMode = PorterDuff.Mode.SRC_ATOP;

            if (progressDrawable != 0) {
                seekBar.setProgressDrawable(getResources().getDrawable(progressDrawable));
            } else {
                LayerDrawable layerDrawable = (LayerDrawable) seekBar.getProgressDrawable();
                if (progressColor != 0) {
                    Drawable progress = layerDrawable.findDrawableByLayerId(android.R.id.progress);
                    progress.setColorFilter(getColorValue(progressColor), mMode);
                    layerDrawable.setDrawableByLayerId(android.R.id.progress, progress);
                }

                if (progressBackgroundColor != 0) {
                    Drawable background = layerDrawable.findDrawableByLayerId(android.R.id.background);
                    background.setColorFilter(getColorValue(progressBackgroundColor), mMode);
                    layerDrawable.setDrawableByLayerId(android.R.id.background, background);
                }
            }

            seekBar.setMax(maxValueSeekBar);
            seekBar.setProgress(progressValueSeekBar[0]);

            if (!isHideInfo) {
                llInfo.setVisibility(View.VISIBLE);
                if (infoSize != 0) {
                    LayoutParams params = (LayoutParams) tvInfo.getLayoutParams();
                    params.width = infoSize;
                    tvInfo.setLayoutParams(params);
                }

                if (infoTextSize != 0) {
                    tvInfo.setTextSize(TypedValue.COMPLEX_UNIT_PX, infoTextSize);
                }

                if (infoTextColor != 0) {
                    tvInfo.setTextColor(getColorValue(infoTextColor));
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (presetProgress != -1) {
                            progressValueSeekBar[0] = presetProgress;
                        }
                        setInfoPosition(progressValueSeekBar[0]);
                    }
                }, 300);

                if (!TextUtils.isEmpty(initInfoString)) {
                    tvInfo.setText(initInfoString);
                } else {
                    tvInfo.setText("" + progressValueSeekBar[0]);
                }
            } else {
                llInfo.setVisibility(View.GONE);
            }

        }

        this.addView(view);
    }

    public void setProgress(int value) {
        seekBar.setProgress(value);

    }


    public void setMax(int max) {
        seekBar.setMax(max);
    }

    private int getColorValue(int resId) {
        return ResourcesCompat.getColor(getResources(), resId, null);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        setInfoPosition(progress);
        tvInfo.setText("" + progress);
        if (_seekBarChangeListener != null) {
            _seekBarChangeListener.onProgressChanged(seekBar, progress, b);
        }
    }

    private void setInfoPosition(int progress) {
        int value = getStepValue(progress);
        seekBar.setProgress(value);
        arrow.setX(getSeekBarThumbPosX(seekBar) - arrow.getWidth() / 2);
        tvInfo.setX(getTimePosition(seekBar, tvInfo));
    }

    private int getTimePosition(SeekBar seekBar, View viewInfo) {
        int thumbPos = getSeekBarThumbPosX(seekBar);
        int seekBarWidth = seekBar.getWidth();
        if (thumbPos + viewInfo.getWidth() / 2 >= seekBarWidth) {
            return seekBarWidth - viewInfo.getWidth();
        } else if (thumbPos - viewInfo.getWidth() / 2 <= seekBar.getPaddingLeft()) {
            return (int) seekBar.getX();
        } else {
            return thumbPos - viewInfo.getWidth() / 2;
        }
    }

    private int getStepValue(int progress) {
        int stepSize = 1;
        return (Math.round(progress / stepSize)) * stepSize;
    }

    private int getSeekBarThumbPosX(SeekBar seekBar) {
        int width = seekBar.getWidth() - seekBar.getPaddingLeft() - seekBar.getPaddingRight();
        int thumbPosX = seekBar.getPaddingLeft() + width * seekBar.getProgress() / seekBar.getMax();
        return thumbPosX;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (_seekBarChangeListener != null) {
            _seekBarChangeListener.onStartTrackingTouch(seekBar);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (_seekBarChangeListener != null) {
            _seekBarChangeListener.onStopTrackingTouch(seekBar);
        }
    }

    public void setSeekBarChangeListener(SeekBar.OnSeekBarChangeListener seekBarChangeListener) {
        _seekBarChangeListener = seekBarChangeListener;
    }

    public void setInfoText(String text, int progress) {
        tvInfo.setText(text);
        setInfoPosition(progress);
    }

    public void presetProgress(int volume) {
        presetProgress = volume;
    }
}
