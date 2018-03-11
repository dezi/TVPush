package de.xavaro.android.gui.base;

import android.annotation.SuppressLint;

import android.support.v7.app.AppCompatActivity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.PixelFormat;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.view.WindowManager;
import android.view.KeyEvent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.smart.GUIRegistration;

@SuppressLint("Registered")
public class GUIActivity extends AppCompatActivity
{
    private final static String LOGTAG = GUIActivity.class.getSimpleName();

    public FrameLayout topframe;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //
        // Remove stupid menu bar.
        //

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        //
        // Window manager layout params.
        //

        WindowManager.LayoutParams params = getWindow().getAttributes();

        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        params.format = PixelFormat.TRANSLUCENT;
        params.dimAmount = 0.0f;
        params.alpha = 1.0f;

        //
        // Setup background and sizes.
        //

        int width = Simple.getDeviceWidth();
        int height = Simple.getDeviceHeight();

        if (Simple.isTV())
        {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (Simple.isTablet())
        {
            params.width = width;
            params.height = 80;
            params.gravity = Gravity.BOTTOM;

            getWindow().setBackgroundDrawable(new ColorDrawable(0x44440000));
            
            setFinishOnTouchOutside(true);
        }

        if (Simple.isPhone())
        {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }

        getWindow().setAttributes(params);

        //
        // Create master frame with exact sizes.
        //

        topframe = new FrameLayout(this);
        topframe.setLayoutParams(new FrameLayout.LayoutParams(width, height, Gravity.TOP));

        setContentView(topframe);
    }

    @Override
    public void onStart()
    {
        Log.d(LOGTAG, "onStart:");

        super.onStart();

        for (int inx = 0; inx < topframe.getChildCount(); inx++)
        {
            if (topframe.getChildAt(inx) instanceof GUIWidget)
            {
                ((GUIWidget) topframe.getChildAt(inx)).onStart();
            }
        }
    }

    @Override
    public void onResume()
    {
        Log.d(LOGTAG, "onResume:");

        super.onResume();

        setUiFlags();

        for (int inx = 0; inx < topframe.getChildCount(); inx++)
        {
            if (topframe.getChildAt(inx) instanceof GUIWidget)
            {
                ((GUIWidget) topframe.getChildAt(inx)).onResume();
            }
        }
    }

    @Override
    public void onPause()
    {
        Log.d(LOGTAG, "onPause:");

        super.onPause();

        for (int inx = 0; inx < topframe.getChildCount(); inx++)
        {
            if (topframe.getChildAt(inx) instanceof GUIWidget)
            {
                ((GUIWidget) topframe.getChildAt(inx)).onPause();
            }
        }
    }

    @Override
    public void onStop()
    {
        Log.d(LOGTAG, "onStop:");

        super.onStop();

        for (int inx = 0; inx < topframe.getChildCount(); inx++)
        {
            if (topframe.getChildAt(inx) instanceof GUIWidget)
            {
                ((GUIWidget) topframe.getChildAt(inx)).onStop();
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);

        setUiFlags();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_SEARCH)
        {
            if (GUIRegistration.speechRecognitionActivityClass != null)
            {
                if (GUIApplication.getCurrentActivityClass(this) != GUIRegistration.speechRecognitionActivityClass)
                {
                    Intent myIntent = new Intent(this, GUIRegistration.speechRecognitionActivityClass);
                    startActivity(myIntent);
                }
            }

            return true;
        }

        Log.d(LOGTAG, "onKeyDown: event=" + event);

        return super.onKeyDown(keyCode, event);
    }

    private void setUiFlags()
    {
        @SuppressLint("InlinedApi")
        int uiOptions
                = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);
    }
}
