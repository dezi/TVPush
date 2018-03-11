package de.xavaro.android.base;

import android.annotation.SuppressLint;

import android.support.v7.app.AppCompatActivity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.PixelFormat;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.view.WindowManager;
import android.view.KeyEvent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity
{
    private final static String LOGTAG = BaseActivity.class.getSimpleName();

    @SuppressLint("InlinedApi")
    private final int uiOptions
            = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

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

        //
        // Remove stupid menu bar.
        //

        if (getSupportActionBar() != null) getSupportActionBar().hide();

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

        setUiFlags();
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
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);

        setUiFlags();
    }

    public void setUiFlags()
    {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_SEARCH)
        {
            if (BaseRegistration.speechRecognitionActivityClass != null)
            {
                if (BaseApplication.getCurrentActivityClass(this) != BaseRegistration.speechRecognitionActivityClass)
                {
                    Intent myIntent = new Intent(this, BaseRegistration.speechRecognitionActivityClass);
                    startActivity(myIntent);
                }
            }

            return true;
        }

        Log.d(LOGTAG, "onKeyDown: event=" + event);

        return super.onKeyDown(keyCode, event);
    }
}
