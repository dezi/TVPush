package de.xavaro.android.common;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.PixelFormat;
import android.graphics.Color;
import android.widget.FrameLayout;
import android.view.WindowManager;
import android.view.KeyEvent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import de.xavaro.android.tvpush.SpeechActivity;

@SuppressLint("Registered")
public class ActBase extends AppCompatActivity
{
    private final static String LOGTAG = ActBase.class.getSimpleName();

    public FrameLayout topframe;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setFinishOnTouchOutside(false);

        WindowManager.LayoutParams params = getWindow().getAttributes();

        params.alpha = 1.0f;
        params.dimAmount = 0.0f;
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        params.format = PixelFormat.TRANSLUCENT;

        getWindow().setAttributes(params);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        topframe = new FrameLayout(this);
        setContentView(topframe);
    }

    @Override
    public void onStart()
    {
        Log.d(LOGTAG, "onStart:");

        super.onStart();
    }

    @Override
    public void onResume()
    {
        Log.d(LOGTAG, "onResume:");

        super.onResume();
    }

    @Override
    public void onPause()
    {
        Log.d(LOGTAG, "onPause:");

        super.onPause();
    }

    @Override
    public void onStop()
    {
        Log.d(LOGTAG, "onStop:");

        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        Log.d(LOGTAG, "onKeyDown: keyCode=" + keyCode + " event=" + event);

        if (keyCode == KeyEvent.KEYCODE_SEARCH)
        {
            if (((AppBase) getApplicationContext()).getCurrentActivityClass() != SpeechActivity.class)
            {
                Intent myIntent = new Intent(this, SpeechActivity.class);
                this.startActivity(myIntent);
            }

            return true;
        }

        return false;
    }
}
