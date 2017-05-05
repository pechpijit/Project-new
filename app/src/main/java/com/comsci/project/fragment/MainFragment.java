package com.comsci.project.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.comsci.project.BarLevelDrawable;
import com.comsci.project.MicrophoneInput;
import com.comsci.project.MicrophoneInputListener;
import com.comsci.project.R;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

import az.plainpie.PieView;
import az.plainpie.animation.PieAngleAnimation;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements
        MicrophoneInputListener {

    MicrophoneInput micInput;
    int max_dB = 0;
    TextView mGainTextView;
    double mOffsetdB = 10;  // Offset for bar, i.e. 0 lit LEDs at 10 dB.
    // ตาม Google ASR input requirements state that audio input sensitivity
    // ควรตั้งไว้ 90 dB SPL ที่ 1000 Hz yields RMS of 2500 for
    // ตัวอย่าง 16-bit, i.e. 20 * log_10(2500 / mGain) = 90.
    double mGain = 2500.0 / Math.pow(10.0, 90.0 / 20.0);
    // For displaying error in calibration.
    double mDifferenceFromNominal = 0.0;
    double mRmsSmoothed;  // Temporally filtered version of RMS.
    double mAlpha = 0.9;  // Coefficient of IIR smoothing filter for RMS.
    private int mSampleRate;  // The audio sampling rate to use.
    private int mAudioSource;  // The audio source to use.

    // Variables to monitor UI update and check for slow updates.
    private volatile boolean mDrawing;
    private volatile int mDrawingCollided;

    private static final String TAG = "LevelMeterActivity";
    PieView pieView;
    boolean click = true;
    FancyButton btn_run,btn_pro;
    PieAngleAnimation animation;
    TextView txt_max;
    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        micInput = new MicrophoneInput(this);

        pieView = (PieView) view.findViewById(R.id.pieView);
        mGainTextView = (TextView) view.findViewById(R.id.mGainTextView);
        txt_max = (TextView) view.findViewById(R.id.txt_max);


        pieView.setInnerTextVisibility(View.VISIBLE);
        pieView.setPercentageTextSize(35);
        pieView.setInnerText("0");
        animation = new PieAngleAnimation(pieView);
        animation.setDuration(3000); //This is the duration of the animation in millis
        pieView.startAnimation(animation);

        btn_run = (FancyButton) view.findViewById(R.id.btn_run);
        btn_pro = (FancyButton) view.findViewById(R.id.btn_pro);
        btn_pro.setEnabled(false);

        btn_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pieView.setInnerText("0");
                if (click) {
                    btn_pro.setEnabled(true);
                    btn_run.setText("Stop");
                    click = false;
                    micInput.setSampleRate(8000);
                    micInput.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
                    micInput.start();
                } else {
                    btn_pro.setEnabled(false);
                    btn_run.setText("Start");
                    click = true;
                    micInput.stop();
                    pieView.setPercentage(100);
                    animation.setDuration(2000); //This is the duration of the animation in millis
                    pieView.startAnimation(animation);
                }
            }
        });

        btn_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        // ลบไป 2 dB.
        FancyButton minus5dbButton = (FancyButton)view.findViewById(R.id.btn_c1);
        DbClickListener minus5dBButtonListener = new DbClickListener(-2.0);
        minus5dbButton.setOnClickListener(minus5dBButtonListener);

        // ลบไป 1 dB.
        FancyButton minus1dbButton = (FancyButton)view.findViewById(R.id.btn_c2);
        DbClickListener minus1dBButtonListener = new DbClickListener(-1.0);
        minus1dbButton.setOnClickListener(minus1dBButtonListener);

        // บวกไป 1 dB.
        FancyButton plus1dbButton = (FancyButton)view.findViewById(R.id.btn_c3);
        DbClickListener plus1dBButtonListener = new DbClickListener(1.0);
        plus1dbButton.setOnClickListener(plus1dBButtonListener);

        // บวกไป 2 dB.
        FancyButton plus5dbButton = (FancyButton)view.findViewById(R.id.btn_c4);
        DbClickListener plus5dBButtonListener = new DbClickListener(2.0);
        plus5dbButton.setOnClickListener(plus5dBButtonListener);

        return view;
    }

    private class DbClickListener implements Button.OnClickListener {
        private double gainIncrement;

        public DbClickListener(double gainIncrement) {
            this.gainIncrement = gainIncrement;
        }

        @Override
        public void onClick(View v) {
            mGain *= Math.pow(10, gainIncrement / 20.0);
            mDifferenceFromNominal -= gainIncrement;
            DecimalFormat df = new DecimalFormat("##.# dB");
            mGainTextView.setText(df.format(mDifferenceFromNominal));
        }
    }
    private void readPreferences() {
        SharedPreferences preferences = getActivity().getSharedPreferences("LevelMeter",
                Context.MODE_PRIVATE);
        mSampleRate = preferences.getInt("SampleRate", 8000);
        mAudioSource = preferences.getInt("AudioSource",
                MediaRecorder.AudioSource.VOICE_RECOGNITION);
    }


    @Override
    public void processAudioFrame(short[] audioFrame) {
        if (!mDrawing) {
            mDrawing = true;
            // Compute the RMS value. (Note that this does not remove DC).
            double rms = 0;
            for (int i = 0; i < audioFrame.length; i++) {
                rms += audioFrame[i]*audioFrame[i];
            }
            rms = Math.sqrt(rms/audioFrame.length);

            // Compute a smoothed version for less flickering of the display.
            mRmsSmoothed = mRmsSmoothed * mAlpha + (1 - mAlpha) * rms;
            final double rmsdB = 20.0 * Math.log10(mGain * mRmsSmoothed);

            // Set up a method that runs on the UI thread to update of the LED bar
            // and numerical display.
            pieView.post(new Runnable() {
                @Override
                public void run() {
                    // The bar has an input range of [0.0 ; 1.0] and 10 segments.
                    // Each LED corresponds to 6 dB.
//                    mBarLevel.setLevel((mOffsetdB + rmsdB) / 60);

                    DecimalFormat df = new DecimalFormat("##");
                    String temp = df.format(20 + rmsdB);
                    int db = Integer.parseInt(temp);

                    if (db > max_dB) {
                        max_dB = db;
                        txt_max.setText(max_dB + " dB");
                    }

                    pieView.setInnerText(temp);
                    int peri = (130 / 100) * db;

                    pieView.setPercentage(peri);
                    pieView.setPieInnerPadding(peri);
                    pieView.setInnerText(String.valueOf(db));
//                    mdBTextView.setText(temp);

                    DecimalFormat df_fraction = new DecimalFormat("#");
                    int one_decimal = (int) (Math.round(Math.abs(rmsdB * 10))) % 10;
//                    mdBFractionTextView.setText(Integer.toString(one_decimal));
                    mDrawing = false;
                }
            });
        } else {
            mDrawingCollided++;
            Log.v(TAG, "Level bar update collision, i.e. update took longer " +
                    "than 20ms. Collision count" + Double.toString(mDrawingCollided));
        }
    }
}
