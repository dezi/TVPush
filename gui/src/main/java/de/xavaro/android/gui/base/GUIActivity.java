package de.xavaro.android.gui.base;

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
